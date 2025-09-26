package top.zzh.util;

import io.jsonwebtoken.security.Keys;
import lombok.var;

import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // 生成安全的512位密钥
        var key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("安全密钥: " + base64Key);
        System.out.println("密钥长度: " + base64Key.length() + " 字符");
    }
}
