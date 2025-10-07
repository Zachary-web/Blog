package top.zzh.util.comment.channel;

import top.zzh.model.dto.Comment;

/**
 * 评论提醒方式
 *
 * @author Z
 * @date: 2022-01-22
 */
public interface CommentNotifyChannel {
	/**
	 * 通过指定方式通知自己
	 *
	 * @param comment 当前收到的评论
	 */
	void notifyMyself(Comment comment);
}
