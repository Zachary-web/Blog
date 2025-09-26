package top.zzh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.zzh.entity.Friend;

import java.util.List;

/**
 * @Description: 友链持久层接口
 * @author Z
 * @Date: 2020-09-08
 */
@Mapper
@Repository
public interface FriendMapper {
	List<Friend> getFriendList();

	List<top.zzh.model.vo.Friend> getFriendVOList();

	int updateFriendPublishedById(Long id, Boolean published);

	int saveFriend(Friend friend);

	int updateFriend(top.zzh.model.dto.Friend friend);

	int deleteFriend(Long id);

	int updateViewsByNickname(String nickname);
}
