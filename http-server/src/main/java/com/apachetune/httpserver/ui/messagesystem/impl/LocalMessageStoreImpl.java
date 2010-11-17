package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageStore;
import com.apachetune.httpserver.ui.messagesystem.MessageStoreDataChangedListener;
import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.apachetune.core.utils.Utils.close;
import static com.apachetune.httpserver.Constants.MESSAGE_STORE_DB_URL_PROP_NAME;
import static java.util.Collections.emptyList;

/**
 * FIXDOC
 */
public class LocalMessageStoreImpl implements MessageStore {
    private static final Logger logger = LoggerFactory.getLogger(LocalMessageStoreImpl.class);

    private static final String NEWS_MESSAGE_TABLE_RECREATE_SQL =
            "create table if not exists news_messages (tstmp bigint primary key, subject varchar, content nvarchar," +
                    " unread boolean)";

    private static final String PROPS_TABLE_RECREATE_SQL =
            "create table if not exists props (prop_name varchar primary key, prop_value varchar)";

    private static final String INSERT_NEWS_MESSAGE_SQL =
            "insert into news_messages (tstmp, subject, content, unread) values (?, ?, ?, ?)";

    private static final String UPDATE_NEWS_MESSAGE_SQL =
            "update news_messages set tstmp = ?, subject = ?, content = ?, unread = ? where tstmp = ?";

    private static final String SELECT_ALL_NEWS_MESSAGES_SQL =
            "select tstmp, subject, content, unread from news_messages order by tstmp desc";

    private static final String SELECT_UNREAD_NEWS_MESSAGES_SQL =
            "select tstmp, subject, content, unread from news_messages where unread = TRUE order by tstmp desc";

    private static final String DELETE_ALL_NEWS_MESSAGES_SQL = "delete from news_messages";

    private static final String DELETE_NEWS_MESSAGE_SQL = "delete from news_messages where tstmp = ?";

    private static final String GET_LAST_TIMESTAMP_FROM_PROPS_SQL =
            "select prop_value from props where prop_name = 'last_stored_timestamp'";

    private static final String UPDATE_NEW_LAST_TIMESTAMP_IN_PROPS_SQL =
            "update props set prop_value = ? where prop_name = 'last_stored_timestamp'";

    private static final String STORE_NEW_LAST_TIMESTAMP_TO_PROPS_SQL =
            "insert into props (prop_name, prop_value) values (?, ?)";

    private final String dbUrl;

    private List<MessageStoreDataChangedListener> dataChangedListeners =
            new ArrayList<MessageStoreDataChangedListener>();

    private Connection connection;

    private boolean isInitialized;

    @Inject
    public LocalMessageStoreImpl(@Named(MESSAGE_STORE_DB_URL_PROP_NAME) String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public final void initialize() throws SQLException {
        connection = DriverManager.getConnection(dbUrl);

        connection.setAutoCommit(false);

        createSchema();

        isInitialized = true;
    }

    @Override
    public final void dispose() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public final MessageTimestamp getLastTimestamp() {
        if (!checkInitialized()) {
            return MessageTimestamp.createEmpty();
        }

        Statement st = null;

        ResultSet rs = null;

        try {
            st = connection.createStatement();

            rs = st.executeQuery(GET_LAST_TIMESTAMP_FROM_PROPS_SQL);

            if (rs.next()) {
                return MessageTimestamp.create(Long.parseLong(rs.getString(1)));
            } else {
                return MessageTimestamp.createEmpty();
            }
        } catch (SQLException e) {
            logger.error("Error during getting last stored timestamp of message.", e);

            return MessageTimestamp.createEmpty();
        } finally {
            close(rs);
            close(st);
        }
    }

    @Override
    public final List<NewsMessage> getMessages() {
        if (!checkInitialized()) {
            return emptyList();
        }

        Statement st = null;

        ResultSet rs = null;

        try {
            st = connection.createStatement();

            rs = st.executeQuery(SELECT_ALL_NEWS_MESSAGES_SQL);

            return resultSetToMessageList(rs);
        } catch (SQLException e) {
            logger.error("Error during loading stored messages.", e);

            return emptyList();
        } finally {
            close(rs);
            close(st);
        }
    }

    @Override
    public final List<NewsMessage> getUnreadMessages() {
        if (!checkInitialized()) {
            return emptyList();
        }

        Statement st = null;

        ResultSet rs = null;

        try {
            st = connection.createStatement();

            rs = st.executeQuery(SELECT_UNREAD_NEWS_MESSAGES_SQL);

            return resultSetToMessageList(rs);
        } catch (SQLException e) {
            logger.error("Error during loading stored unread messages.", e);

            return emptyList();
        } finally {
            close(rs);
            close(st);
        }
    }

    @Override
    public final void storeMessages(Collection<NewsMessage> messages) {
        if (!checkInitialized()) {
            return;
        }

        if (messages.isEmpty()) {
            return;
        }

        long newMaxTimestamp = -1;

        try {
            for (NewsMessage msg : messages) {
                if (updateMessage(msg) == 0) {
                    insertMessage(msg);

                    newMaxTimestamp = Math.max(newMaxTimestamp, msg.getTimestamp().getValue());
                }
            }

            connection.commit();
        } catch (SQLException e) {
            logger.error("Error during storing news messages.", e);
        }

        if (newMaxTimestamp == -1) {
            return;
        }

        try {
            if (getLastTimestamp().isEmpty()) {
                storeNewLastTimestamp(newMaxTimestamp);

                connection.commit();
            } else if (getLastTimestamp().getValue() < newMaxTimestamp) {
                updateNewLastTimestamp(newMaxTimestamp);

                connection.commit();
            }
        } catch (SQLException e) {
            logger.error("Error during storing news messages.", e);
        }

        notifyDataChanged();
    }

    @Override
    public final void deleteMessages(Collection<NewsMessage> messages) {
        if (!checkInitialized()) {
            return;
        }

        if (messages.isEmpty()) {
            return;
        }

        try {
            for (NewsMessage msg : messages) {
                deleteMessage(msg);

                connection.commit();
            }
        } catch (SQLException e) {
            logger.error("Error during deleting news messages from store.", e);
        }

        notifyDataChanged();
    }

    @Override
    public final void deleteAllMessages() {
        if (!checkInitialized()) {
            return;
        }

        Statement st = null;

        try {
            st = connection.createStatement();

            st.execute(DELETE_ALL_NEWS_MESSAGES_SQL);

            connection.commit();
        } catch (SQLException e) {
            logger.error("Error during deleting all news messages from store.", e);
        } finally {
            close(st);
        }

        notifyDataChanged();
    }

    @Override
    public final void addDataChangedListener(MessageStoreDataChangedListener listener) {
        dataChangedListeners.add(listener);
    }

    @Override
    public final void removeDataChangedListener(MessageStoreDataChangedListener listener) {
        dataChangedListeners.remove(listener);
    }

    private void notifyDataChanged() {
        List<MessageStoreDataChangedListener> listeners =
                new ArrayList<MessageStoreDataChangedListener>(dataChangedListeners);

        for (MessageStoreDataChangedListener listener : listeners) {
            listener.onStoredDataChanged();
        }
    }

    private void updateNewLastTimestamp(long newMaxTimestamp) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_NEW_LAST_TIMESTAMP_IN_PROPS_SQL);

        try {
            ps.setString(1, "" + newMaxTimestamp);

            ps.execute();
        } finally {
            ps.close();
        }
    }

    private void storeNewLastTimestamp(long newMaxTimestamp) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(STORE_NEW_LAST_TIMESTAMP_TO_PROPS_SQL);

        try {
            ps.setString(1, "last_stored_timestamp");
            ps.setString(2, "" + newMaxTimestamp);

            ps.execute();
        } finally {
            ps.close();
        }
    }

    private void deleteMessage(NewsMessage msg) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_NEWS_MESSAGE_SQL);

        try {
            ps.setLong(1, msg.getTimestamp().getValue());

            ps.execute();
        } finally {
            ps.close();
        }
    }

    private List<NewsMessage> resultSetToMessageList(ResultSet rs) throws SQLException {
        List<NewsMessage> resultList = new ArrayList<NewsMessage>();

        while (rs.next()) {
            NewsMessage msg = new NewsMessage(
                    MessageTimestamp.create(rs.getLong(1)),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getBoolean(4)
            );

            resultList.add(msg);
        }

        return resultList;
    }

    private void insertMessage(NewsMessage msg) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_NEWS_MESSAGE_SQL);

        try {
            ps.setLong(1, msg.getTimestamp().getValue());
            ps.setString(2, msg.getSubject());
            ps.setString(3, msg.getContent());
            ps.setBoolean(4, msg.isUnread());

            ps.execute();
        } finally {
            ps.close();
        }
    }

    private int updateMessage(NewsMessage msg) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_NEWS_MESSAGE_SQL);

        try {
            ps.setLong(1, msg.getTimestamp().getValue());
            ps.setString(2, msg.getSubject());
            ps.setString(3, msg.getContent());
            ps.setBoolean(4, msg.isUnread());
            ps.setLong(5, msg.getTimestamp().getValue());

            return ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void createSchema() throws SQLException {
        Statement st = connection.createStatement();

        try {
            st.execute(NEWS_MESSAGE_TABLE_RECREATE_SQL);
            st.execute(PROPS_TABLE_RECREATE_SQL);

            connection.commit();
        } finally {
            st.close();
        }
    }

    private boolean checkInitialized() {
        if (!isInitialized) {
            logger.error("Local news message database was not initialized.");
        }

        return isInitialized;
    }
}
