package com.heartsuit.druid;

import com.alibaba.druid.filter.config.ConfigTools;
import org.springframework.stereotype.Component;

/**
 * @Author Heartsuit
 * @Date 2020-05-22
 */
@Component
public class EncryptDecrypt {
    public void encrypt() throws Exception {
        String password = "root";
        String[] keyPair = ConfigTools.genKeyPair(512);
        String privateKey = keyPair[0];
        String publicKey = keyPair[1];
        System.out.println("私    钥： " + privateKey);
        System.out.println("公    钥： " + publicKey);
        String encryptPassword = ConfigTools.encrypt(privateKey, password);
        System.out.println("密文密码： " + encryptPassword);

        /** 输出：
         * 私    钥： MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAmsdCBWKd4FRoGJ0+PMtIepI8LWAHd3TysWGKOlfvF8UtXqOv8agodlQ4jcbPH6JoVRcEkgpqteWhj/v7vwc61QIDAQABAkB5ockCTmNfHTXI0hlM0TueBzl/Nw3nFGJ8fviPrPbZqAM6OTZNuA8Uka7AAU5MpbwYbrNcRjXqT5RzaicNrOPBAiEA0BuYwQmRzYLY27xkY99BfLQqwUimR3kwowBUHToV3/0CIQC+ZdNJ71zTW5WDARUz8B8vOBZYfJx25qrCzHbL3DfhuQIhAMwyF9tpeV/uQLyzCMoaONaUrdMDZuyAlGGMI/ydjvM9AiBb650OXNlb0SNlk+hAovTrPxDKt55yaPqYAU55LWBtQQIgYsLxtClktL+ZgVQkGL7Rqa44E7L1TYHl8zyBSbaeYiQ=
         * 公    钥： MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJrHQgVineBUaBidPjzLSHqSPC1gB3d08rFhijpX7xfFLV6jr/GoKHZUOI3Gzx+iaFUXBJIKarXloY/7+78HOtUCAwEAAQ==
         * 密文密码： VFDsVA2tKIonKgNtPHadtBljYkuA1K6PnW8hTy1ZzADbRldd280Z/nbHv5TW9J7JZyK/q411Sg1GE4elxKoYcQ==
         * */
    }

    public void decrypt() throws Exception {
        String encrypted = "VFDsVA2tKIonKgNtPHadtBljYkuA1K6PnW8hTy1ZzADbRldd280Z/nbHv5TW9J7JZyK/q411Sg1GE4elxKoYcQ==";
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJrHQgVineBUaBidPjzLSHqSPC1gB3d08rFhijpX7xfFLV6jr/GoKHZUOI3Gzx+iaFUXBJIKarXloY/7+78HOtUCAwEAAQ==";

        String decrypted = ConfigTools.decrypt(publicKey, encrypted);
        System.out.println("解密之后：" + decrypted);
        /** 输出：
         * 解密之后：root
         * */
    }
}
