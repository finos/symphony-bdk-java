package com.symphony.ms.songwriter.internal.webapi.accesscontrol;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Service;

@Service
public class HashService {
  /**
   * Method to check if given token is equal the encrypt hash
   * @param token clear text hashedPassword to be hashed
   */
  public boolean checkToken(String token, String hash) {
    return token.equals(hash);
  }

  /**
   * Method to generate a hash to be used as token between monitoring endpoints and yaml
   * @return a hash
   * @throws NoSuchAlgorithmException when the instance of SHA-512 can't be get
   */
  public String generateHash(String token, String salt) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
    messageDigest.update(token.concat(salt).getBytes());
    return DatatypeConverter.printHexBinary(messageDigest.digest());
  }

}
