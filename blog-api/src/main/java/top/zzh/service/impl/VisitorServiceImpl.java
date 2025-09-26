package top.zzh.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zzh.constant.RedisKeyConstants;
import top.zzh.entity.Visitor;
import top.zzh.exception.PersistenceException;
import top.zzh.mapper.VisitorMapper;
import top.zzh.model.dto.UserAgentDTO;
import top.zzh.model.dto.VisitLogUuidTime;
import top.zzh.service.RedisService;
import top.zzh.service.VisitorService;
import top.zzh.util.IpAddressUtils;
import top.zzh.util.UserAgentUtils;

import java.util.List;

/**
 * @Description: 访客统计业务层实现
 * @author Z
 * @Date: 2021-01-31
 */
@Service
public class VisitorServiceImpl implements VisitorService {
	@Autowired
	VisitorMapper visitorMapper;
	@Autowired
	RedisService redisService;
	@Autowired
	UserAgentUtils userAgentUtils;

	@Override
	public List<Visitor> getVisitorListByDate(String startDate, String endDate) {
		return visitorMapper.getVisitorListByDate(startDate, endDate);
	}

	@Override
	public List<String> getNewVisitorIpSourceByYesterday() {
		return visitorMapper.getNewVisitorIpSourceByYesterday();
	}

	@Override
	public boolean hasUUID(String uuid) {
		return visitorMapper.hasUUID(uuid) != 0;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveVisitor(Visitor visitor) {
		String ipSource = IpAddressUtils.getCityInfo(visitor.getIp());
		UserAgentDTO userAgentDTO = userAgentUtils.parseOsAndBrowser(visitor.getUserAgent());
		visitor.setIpSource(ipSource);
		visitor.setOs(userAgentDTO.getOs());
		visitor.setBrowser(userAgentDTO.getBrowser());
		if (visitorMapper.saveVisitor(visitor) != 1) {
			throw new PersistenceException("访客添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updatePVAndLastTimeByUUID(VisitLogUuidTime dto) {
		visitorMapper.updatePVAndLastTimeByUUID(dto);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteVisitor(Long id, String uuid) {
		//删除Redis中该访客的uuid
		redisService.deleteValueBySet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
		if (visitorMapper.deleteVisitorById(id) != 1) {
			throw new PersistenceException("删除访客失败");
		}
	}
}
