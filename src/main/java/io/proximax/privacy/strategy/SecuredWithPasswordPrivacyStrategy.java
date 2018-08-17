package io.proximax.privacy.strategy;

import io.proximax.cipher.BinaryPBKDF2CipherEncryption;
import io.proximax.exceptions.DecryptionFailureException;
import io.proximax.exceptions.EncryptionFailureException;
import io.proximax.model.PrivacyType;

import static io.proximax.utils.ParameterValidationUtils.checkParameter;

/**
 * The privacy strategy that secures the data using a long password, 50 characters minimum.
 */
public final class SecuredWithPasswordPrivacyStrategy extends AbstractPlainMessagePrivacyStrategy {

    private static final int MINIMUM_PASSWORD_LENGTH = 50;

    private final BinaryPBKDF2CipherEncryption encryptor;

    private final char[] password;

    SecuredWithPasswordPrivacyStrategy(BinaryPBKDF2CipherEncryption encryptor, String password, String searchTag) {
        super(searchTag);

        checkParameter(password != null, "password is required");
        checkParameter(password.length() >= MINIMUM_PASSWORD_LENGTH, "minimum length for password is 50");

        this.encryptor = encryptor;
        this.password = password.toCharArray();
    }

    /**
     * Get the privacy type which is set as PASSWORD
     * @return the privacy type's int value
     * @see PrivacyType
     */
    @Override
    public int getPrivacyType() {
        return PrivacyType.PASSWORD.getValue();
    }

    /**
     * Encrypt the data with password
     * @param data data to encrypt
     * @return the encrypted data
     */
    @Override
    public final byte[] encryptData(byte[] data) {
        try {
            return encryptor.encrypt(data, password);
        } catch (Exception e) {
            throw new EncryptionFailureException("Exception encountered encrypting data", e);
        }
    }

    /**
     * Decrypt the data with password
     * @param data data to encrypt
     * @return the decrypted data
     */
    @Override
    public final byte[] decryptData(byte[] data) {
        try {
            return encryptor.decrypt(data, password);
        } catch (Exception e) {
            throw new DecryptionFailureException("Exception encountered decrypting data", e);
        }
    }

    /**
     * Create instance of this strategy
     * @param password the password
     * @param searchTag an optional search tag
     * @return the instance of this strategy
     */
    public static SecuredWithPasswordPrivacyStrategy create(String password, String searchTag) {
        return new SecuredWithPasswordPrivacyStrategy(new BinaryPBKDF2CipherEncryption(), password, searchTag);
    }
}