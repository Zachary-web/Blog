package top.zzh.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

/**
 * @Description: JWT工具类
 * @author Z
 * @Date: 2020-09-02
 */
@Component
public class JwtUtils {
	private static long expireTime;
	private static String secretKey;

	@Value("${token.secretKey}")
	public void setSecretKey(String secretKey) {
		JwtUtils.secretKey = secretKey;
	}

	@Value("${token.expireTime}")
	public void setExpireTime(long expireTime) {
		JwtUtils.expireTime = expireTime;
	}

	/**
	 * 将配置的base64密钥转换为安全的SecretKey
	 */
	private static SecretKey getSigningKey() {
		try {
			// 检查密钥是否为空
			if (secretKey == null || secretKey.trim().isEmpty()) {
				throw new IllegalArgumentException("JWT密钥未配置，请检查token.secretKey配置");
			}

			// 解码base64密钥
			byte[] keyBytes = Base64.getDecoder().decode(secretKey.trim());

			// 使用Keys工具类创建安全的签名密钥
			return Keys.hmacShaKeyFor(keyBytes);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("JWT密钥格式错误，请确保配置的是有效的base64编码字符串", e);
		} catch (Exception e) {
			throw new RuntimeException("JWT密钥处理失败", e);
		}
	}

	/**
	 * 判断token是否存在
	 *
	 * @param token
	 * @return
	 */
	public static boolean judgeTokenIsExist(String token) {
		return token != null && !"".equals(token) && !"null".equals(token);
	}

	/**
	 * 生成token
	 *
	 * @param subject
	 * @return
	 */
	public static String generateToken(String subject) {
		String jwt = Jwts.builder()
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(getSigningKey()) // 使用安全的签名方式
				.compact();
		return jwt;
	}

	/**
	 * 生成带角色权限的token
	 *
	 * @param subject
	 * @param authorities
	 * @return
	 */
	public static String generateToken(String subject, Collection<? extends GrantedAuthority> authorities) {
		StringBuilder sb = new StringBuilder();
		for (GrantedAuthority authority : authorities) {
			sb.append(authority.getAuthority()).append(",");
		}
		String jwt = Jwts.builder()
				.setSubject(subject)
				.claim("authorities", sb)
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(getSigningKey()) // 使用安全的签名方式
				.compact();
		return jwt;
	}

	/**
	 * 生成自定义过期时间token
	 *
	 * @param subject
	 * @param expireTime
	 * @return
	 */
	public static String generateToken(String subject, long expireTime) {
		String jwt = Jwts.builder()
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(getSigningKey()) // 使用安全的签名方式
				.compact();
		return jwt;
	}

	/**
	 * 获取tokenBody同时校验token是否有效（无效则会抛出异常）
	 *
	 * @param token
	 * @return
	 */
	public static Claims getTokenBody(String token) {
		// 清理token字符串（移除Bearer前缀和空格）
		String cleanedToken = token.replace("Bearer", "").trim();

		Claims claims = Jwts.parserBuilder()
				.setSigningKey(getSigningKey()) // 使用相同的密钥解析
				.build()
				.parseClaimsJws(cleanedToken)
				.getBody();
		return claims;
	}

	/**
	 * 安全地验证token是否有效
	 *
	 * @param token
	 * @return true如果token有效，false如果无效或过期
	 */
	public static boolean validateToken(String token) {
		try {
			if (!judgeTokenIsExist(token)) {
				return false;
			}

			String cleanedToken = token.replace("Bearer", "").trim();
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(cleanedToken);
			return true;
		} catch (Exception e) {
			// 记录调试信息（可选）
			// System.out.println("Token验证失败: " + e.getMessage());
			return false;
		}
	}

	/**
	 * 从token中获取用户名（subject）
	 *
	 * @param token
	 * @return 用户名
	 */
	public static String getUsernameFromToken(String token) {
		try {
			String cleanedToken = token.replace("Bearer", "").trim();
			return Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(cleanedToken)
					.getBody()
					.getSubject();
		} catch (Exception e) {
			throw new RuntimeException("从token中提取用户名失败", e);
		}
	}

	/**
	 * 检查token是否即将过期（在指定毫秒数内过期）
	 *
	 * @param token
	 * @param millis 毫秒数
	 * @return true如果即将过期
	 */
	public static boolean isTokenExpiringSoon(String token, long millis) {
		try {
			Claims claims = getTokenBody(token);
			Date expiration = claims.getExpiration();
			long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
			return timeUntilExpiration <= millis;
		} catch (Exception e) {
			return true; // 如果解析失败，视为需要刷新
		}
	}
}
