package com.cookandroid.capstone.Fragment.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoomDTO implements Serializable {

   String roomId;
   ArrayList<UserDTO> users = new ArrayList<>();
   ChatDTO lastChat;

   public void setLastChat(ChatDTO lastChat) {
      this.lastChat = lastChat;
   }

   public ChatDTO getLastChat() {
      return lastChat;
   }

   public String getRoomId() {
      return roomId;
   }

   public void setRoomId(String roomId) {
      this.roomId = roomId;
   }

   public ArrayList<UserDTO> getUsers() {
      return users;
   }

   public void setUsers(ArrayList<UserDTO> users) {
      this.users = users;
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
