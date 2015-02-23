package com.cinebrah.cinebrah.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.net.GcmIntentService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatFragment extends ListFragment implements EditText.OnEditorActionListener {

    private static final String LOG_TAG = "ChatFragment";
    private final static String DONE_KEY_LABEL = "Send";
    @InjectView(R.id.edit_text_chat_input)
    EditText mChatInput;
    ArrayList<GcmIntentService.ChatMessageEvent> chatMessages;
    ChatMessageAdapter mAdapter;
    boolean hideRoomController = false;

    public ChatFragment() {
        chatMessages = new ArrayList<GcmIntentService.ChatMessageEvent>();
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, view);
        mChatInput.setOnEditorActionListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BaseApplication.getBus().register(this);
        mAdapter = new ChatMessageAdapter(activity, chatMessages);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            String message = getChatMessage();
            if (!message.isEmpty()) {
                Log.v(LOG_TAG, "Sending message: " + message);
//                BaseApplication.getApiService().sendChatMessage(message);
                mChatInput.setText("");
            }
        }
        return false;
    }

    private String getChatMessage() {
        return mChatInput.getText().toString();
    }

    @Subscribe
    public void onReceivedChatMessageEvent(GcmIntentService.ChatMessageEvent event) {
        //Only Display a max of 50 messages in chat
        if (mAdapter.getCount() > 50) {
            mAdapter.removeItem(0);
        }
        mAdapter.addItem(event);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    class ChatMessageAdapter extends BaseAdapter {

        Context context;
        ArrayList<GcmIntentService.ChatMessageEvent> messageEvents;

        ChatMessageAdapter(Context context, ArrayList<GcmIntentService.ChatMessageEvent> messageEvents) {
            super();
            this.context = context;
            this.messageEvents = messageEvents;
        }

        @Override
        public int getCount() {
            return messageEvents.size();
        }

        @Override
        public GcmIntentService.ChatMessageEvent getItem(int i) {
            return messageEvents.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder viewHolder;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.list_item_chat_message, null);
                viewHolder = new ViewHolder(row);
                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) row.getTag();
            }
            TextView chatMessageText = viewHolder.getChatMessageTV();

            GcmIntentService.ChatMessageEvent messageEvent = messageEvents.get(position);

            //This will take the chat string and make the username bold
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            stringBuilder.append(messageEvent.getSender()).append(": ");
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, messageEvent.getSender().length() + 2
                    , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(messageEvent.getChatMessage());
            chatMessageText.setText(stringBuilder);

            //This changes the color of the text depending on the type of message it is
            //Normal user is black, server messages are blue
            if (messageEvent.getMessageType().equals("1")) {
                chatMessageText.setTextColor(getResources().getColor(android.R.color.black));
            } else if (messageEvent.getMessageType().equals("2")) {
                chatMessageText.setTextColor(getResources().getColor(R.color.blue));
            }
            return row;
        }

        public void addItem(GcmIntentService.ChatMessageEvent messageEvent) {
            chatMessages.add(messageEvent);
        }

        public void removeItem(int i) {
            chatMessages.remove(i);
        }

        class ViewHolder {
            @InjectView(R.id.text_chat_message)
            TextView chatMessage;

            ViewHolder(View base) {
                ButterKnife.inject(this, base);
            }

            public TextView getChatMessageTV() {
                return chatMessage;
            }
        }


    }
}
