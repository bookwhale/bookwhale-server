package com.teamherb.bookstoreback.chatroom.service;

import com.teamherb.bookstoreback.chatroom.domain.ChatRoom;
import com.teamherb.bookstoreback.chatroom.domain.ChatRoomRepository;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomCreateRequest;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomResponse;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;

  private final UserRepository userRepository;

  private final PostRepository postRepository;

  public Long createChatRoom(User user, ChatRoomCreateRequest request) {
    User seller = validateSellerIdAndGetSeller(request.getSellerId());
    Post post = validatePostIdAndGetPost(request.getPostId());
    ChatRoom chatRoom = ChatRoom.create(post, user, seller);
    return chatRoomRepository.save(chatRoom).getId();
  }

  public Post validatePostIdAndGetPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_POST_ID));
  }

  public User validateSellerIdAndGetSeller(Long sellerId) {
    return userRepository.findById(sellerId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SELLER_ID));
  }

  @Transactional(readOnly = true)
  public List<ChatRoomResponse> findChatRooms(User user) {
    List<ChatRoom> rooms = chatRoomRepository.findAllByBuyerOrSellerCreatedDateDesc(user);
    return checkRoomsAndGetRoomResponses(rooms, user);
  }

  private List<ChatRoomResponse> checkRoomsAndGetRoomResponses(List<ChatRoom> rooms,
      User loginUser) {
    return rooms.stream()
        //내가 떠난 채팅방은 제외
        .filter(room -> !room.checkIsLeaveChatRoom(loginUser))
        //상대방이 나간 채팅방인지 확인 후 ChatRoomResponse 생성
        .map(room -> ChatRoomResponse.of(room, room.getOpponent(loginUser),
            room.checkIsOpponentLeaveChatRoom(loginUser)))
        .collect(Collectors.toList());
  }

  public void deleteChatRoom(User user, Long roomId) {
    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CHATROOM_ID));
    room.leaveChatRoom(user);
    if (room.isEmptyChatRoom()) {
      chatRoomRepository.deleteById(roomId);
    }
  }
}
