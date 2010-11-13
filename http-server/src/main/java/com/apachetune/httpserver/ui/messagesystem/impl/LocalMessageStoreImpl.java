package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageStore;
import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.apachetune.httpserver.Constants.MESSAGE_STORE_DB_URL_PROP_NAME;

/**
 * FIXDOC
 */
public class LocalMessageStoreImpl implements MessageStore {
    private static final String NEWS_MESSAGE_TABLE_RECREATE_SQL =
            "create table if not exists news_messages (tstmp bigint primary key, subject varchar, content nvarchar," +
                    " unread boolean)";

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

    private final String dbUrl;

    private Connection connection;

    @Inject
    public LocalMessageStoreImpl(@Named(MESSAGE_STORE_DB_URL_PROP_NAME) String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public final void initialize() throws SQLException {
        connection = DriverManager.getConnection(dbUrl);

        connection.setAutoCommit(false);

        createSchema();
    }

    @Override
    public final void dispose() throws SQLException {
        connection.close();
    }

    @Override
    public final MessageTimestamp getLastTimestamp() throws SQLException {
        if (getMessages().size() > 0) {
            Statement st = connection.createStatement();

            try {
                ResultSet rs = st.executeQuery("select max(tstmp) from news_messages");

                rs.next();

                return MessageTimestamp.create(rs.getLong(1));                
            } finally {
                st.close();
            }
        } else {
            return MessageTimestamp.createEmpty();
        }
    }

    @Override
    public final List<NewsMessage> getMessages() throws SQLException {
        Statement st = connection.createStatement();

        try {
            ResultSet rs = st.executeQuery(SELECT_ALL_NEWS_MESSAGES_SQL);

            try {
                return resultSetToMessageList(rs);
            } finally {
                rs.close();
            }
        } finally {
            st.close();
        }
    }

    @Override
    public final List<NewsMessage> getUnreadMessages() throws SQLException {
        Statement st = connection.createStatement();

        try {
            ResultSet rs = st.executeQuery(SELECT_UNREAD_NEWS_MESSAGES_SQL);

            try {
                return resultSetToMessageList(rs);
            } finally {
                rs.close();
            }
        } finally {
            st.close();
        }
    }

    @Override
    public final void storeMessages(List<NewsMessage> messages) throws SQLException {
        for (NewsMessage msg : messages) {
            if (updateMessage(msg) == 0) {
                insertMessage(msg);
            }
        }

        connection.commit();
    }

    @Override
    public final void deleteMessages(List<NewsMessage> messages) throws SQLException {
        for (NewsMessage msg : messages) {
            deleteMessage(msg);

            connection.commit();
        }
    }

    @Override
    public final void deleteAllMessages() throws SQLException {
        Statement st = connection.createStatement();

        try {
            st.execute(DELETE_ALL_NEWS_MESSAGES_SQL);

            connection.commit();
        } finally {
            st.close();
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

            connection.commit();
        } finally {
            st.close();
        }
    }
}
