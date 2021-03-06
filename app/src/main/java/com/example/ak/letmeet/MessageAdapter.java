package com.example.ak.letmeet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;



    public MessageAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageSenderPicture = (ImageView) itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = (ImageView) itemView.findViewById(R.id.message_receiver_image_view);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int i)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder .receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);

        if(fromMessageType.equals("text"))
        {
            /*messageViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);*/

            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
        }
        else if(fromMessageType.equals("image"))
        {
            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
            }
        }

        /*else if(fromMessageType.equals("pdf") || fromMessageType.equals("docx"))// Pdf and Docx k liye
        {
            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load("gs://letmeet-dcfa6.appspot.com/Image Files/file.png")
                        .into(messageViewHolder.messageSenderPicture);
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load("gs://letmeet-dcfa6.appspot.com/Image Files/file.png")
                        .into(messageViewHolder.messageReceiverPicture);

            }
        }*/ // yaha tak




        if(fromUserID.equals(messageSenderId))
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    /*if(userMessagesList.get(i).getType().equals("pdf") || userMessagesList.get(i).getType().equals("docx"))// Yeh If wala Bhi
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download and View",
                                        "Cancel",
                                        "Delete for Everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message??");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int k)
                            {
                                if(k == 0)
                                {
                                    deleteSentMessage(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(k == 1)
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(k == 3)
                                {
                                    deleteMessageForEveryOne(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }*/

                    if(userMessagesList.get(i).getType().equals("image"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "View this Image",
                                        "Cancel",
                                        "Delete for Everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message??");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int k)
                            {
                                if(k == 0)
                                {
                                    deleteSentMessage(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(k == 1)
                                {
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(i).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(k == 3)
                                {
                                    deleteMessageForEveryOne(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }




                    else if(userMessagesList.get(i).getType().equals("text"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Cancel",
                                        "Delete for Everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message??");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int k)
                            {
                                if(k == 0)
                                {
                                    deleteSentMessage(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(k == 2)
                                {
                                    deleteMessageForEveryOne(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        else
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    /*if(userMessagesList.get(i).getType().equals("pdf") || userMessagesList.get(i).getType().equals("docx"))// Yeh If wala Bhi
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download and View",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message??");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int k)
                            {
                                if(k == 0)
                                {
                                    deleteReceiveMessage(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(k == 1)
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }*/

                    if(userMessagesList.get(i).getType().equals("image"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "View this Image",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message??");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int k)
                            {
                                if(k == 0)
                                {
                                    deleteReceiveMessage(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(k == 1)
                                {
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(i).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }




                    else if(userMessagesList.get(i).getType().equals("text"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message??");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int k)
                            {
                                if(k == 0)
                                {
                                    deleteReceiveMessage(i, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }


    }




    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }


    private void deleteSentMessage(final int i, final MessageViewHolder holder)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Message")
                .child(userMessagesList.get(i).getFrom())
                .child(userMessagesList.get(i).getTo())
                .child(userMessagesList.get(i).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Message Deleted Successfully..", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred..", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void deleteReceiveMessage(final int i, final MessageViewHolder holder)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Message")
                .child(userMessagesList.get(i).getTo())
                .child(userMessagesList.get(i).getFrom())
                .child(userMessagesList.get(i).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Message Deleted Successfully..", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred..", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void deleteMessageForEveryOne(final int i, final MessageViewHolder holder)
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Message")
                .child(userMessagesList.get(i).getTo())
                .child(userMessagesList.get(i).getFrom())
                .child(userMessagesList.get(i).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    rootRef.child("Message")
                            .child(userMessagesList.get(i).getFrom())
                            .child(userMessagesList.get(i).getTo())
                            .child(userMessagesList.get(i).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(holder.itemView.getContext(), "Message Deleted Successfully..", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred..", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
