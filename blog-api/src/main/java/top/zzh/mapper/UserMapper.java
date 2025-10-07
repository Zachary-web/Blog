package top.zzh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.zzh.entity.User;

/**
 * @Description: 用户持久层接口
 * @author Z
 * @Date: 2020-07-19
 */
@Mapper
@Repository
public interface UserMapper {
	User findByUsername(String username);

	User findById(Long id);

	int updateUserByUsername(String username, User user);
}
