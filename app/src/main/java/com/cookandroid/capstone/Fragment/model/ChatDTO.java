package com.cookandroid.capstone.Fragment.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ChatDTO implements Serializable {
   String chatId;
   long createdAt;
   UserDTO user;
   String chat;

   public long getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(long createdAt) {
      this.createdAt = createdAt;
   }

   public String getChatId() {
      return chatId;
   }

   public void setChatId(String chatId) {
      this.chatId = chatId;
   }

   public UserDTO getUser() {
      return user;
   }

   public void setUser(UserDTO user) {
      this.user = user;
   }

   public String getChat() {
      return chat;
   }

   public void setChat(String chat) {
      this.chat = chat;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      return super.equals(obj);
   }
}
