package com.apachetune.httpserver.ui.messagesystem.messagedialog;

import com.apachetune.core.ui.NPresenter;
import com.apachetune.httpserver.ui.messagesystem.MessageStore;
import com.apachetune.httpserver.ui.messagesystem.MessageStoreDataChangedListener;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.google.common.base.Function;
import com.google.inject.Inject;

import java.awt.*;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.transform;
import static java.util.Arrays.asList;
import static javax.swing.JOptionPane.*;

/**
 * FIXDOC
 */
public class MessagePresenter extends NPresenter<MessageView> implements MessageStoreDataChangedListener {
    private final MessageStore messageStore;

    @Inject
    public MessagePresenter(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    @Override
    public final void onViewReady() {
        messageStore.addDataChangedListener(this);

        updateMessageControls();       
    }

    @Override
    public final void onCloseView() {
        messageStore.removeDataChangedListener(this);
    }

    @Override
    public final void onStoredDataChanged() {
        getView().notifyDataChanged();

        updateMessageControls();
    }

    public final void onSelectMessages() {
        if (getView().getSelectedMessages().size() == messageStore.getMessages().size()) {
            getView().unselectAllMessages();
        } else {
            getView().selectAllMessages();
        }
    }

    public final void onMarkMessagesAsUnread() {
        Collection<NewsMessage> messages = getSelectedMessages();

        messages = transform(messages, new Function<NewsMessage, NewsMessage>() {
            @Override
            public final NewsMessage apply(NewsMessage from) {
                return NewsMessage.createBuilder().copyFrom(from).setUnread(true).build();
            }
        });

        messageStore.storeMessages(messages);

        getView().unselectAllMessages();
    }

    public final void onCurrentMessageChanged(NewsMessage msg) {
        System.out.println(msg != null ? msg.getSubject() : null); // todo
    }

    public final void onMessageDelete(NewsMessage msg) {
        messageStore.deleteMessages(asList(msg));

        updateMessageControls();
    }

    public final void onMessagesDelete() {
        Collection<NewsMessage> messages = getSelectedMessages();

        if (messages.size() >= 2) {
            if (showConfirmDialog(
                    (Component) getView(),
                    "Do you want to delete selected messages?", // TODO localize
                    "Messages deletion", // todo localize
                    YES_NO_OPTION, QUESTION_MESSAGE) == NO_OPTION) {
                return;
            }
        }

        messageStore.deleteMessages(messages);

        updateMessageControls();
    }

    private List<NewsMessage> getSelectedMessages() {
        List<NewsMessage> messages = getView().getSelectedMessages();

        if (messages.size() == 0) {
            NewsMessage msg = getView().getCurrentMessage();

            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    private void updateMessageControls() {
        getView().setMessageControlsEnabled(!messageStore.getMessages().isEmpty());
    }
}
