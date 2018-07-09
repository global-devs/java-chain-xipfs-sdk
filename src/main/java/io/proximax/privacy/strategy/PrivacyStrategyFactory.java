package io.proximax.privacy.strategy;


import java.util.Map;

import io.proximax.cipher.BinaryPBKDF2CipherEncryption;


/**
 * A factory for creating PrivacyStrategy objects.
 */
public class PrivacyStrategyFactory {

    /** The plain privacy strategy. */
    public static AbstractPrivacyStrategy plainPrivacyStrategy;

    /**
     * Instantiates a new privacy strategy factory.
     */
    private PrivacyStrategyFactory() {}

    /**
     * Plain privacy.
     *
     * @return the privacy strategy
     */
    public static AbstractPrivacyStrategy plainPrivacy() {
        if (plainPrivacyStrategy == null)
            plainPrivacyStrategy = new PlainPrivacyStrategy();
        return plainPrivacyStrategy;
    }

    /**
     * Secured with nem keys privacy strategy.
     *
     * @param senderOrReceiverPrivateKey the sender or receiver private key
     * @param receiverOrSenderPublicKey the receiver or sender public key
     * @return the privacy strategy
     */
//    public static PrivacyStrategy securedWithNemKeysPrivacyStrategy(String senderOrReceiverPrivateKey, String receiverOrSenderPublicKey) {
//        return new SecuredWithNemKeysPrivacyStrategy(senderOrReceiverPrivateKey, receiverOrSenderPublicKey);
//    }

    /**
     * Secured with password privacy strategy.
     *
     * @param password the password
     * @return the privacy strategy
     */
    public static AbstractPrivacyStrategy securedWithPasswordPrivacyStrategy(String password) {
        return new SecuredWithPasswordPrivacyStrategy(new BinaryPBKDF2CipherEncryption(), password);
    }

    public static AbstractPrivacyStrategy securedWithShamirSecretSharingPrivacyStrategy(int secretTotalPartCount, int secretMinimumPartCountToBuild,
                                                                                Map<Integer, byte[]> secretParts) {
        return new SecuredWithShamirSecretSharingPrivacyStrategy(new BinaryPBKDF2CipherEncryption(), secretTotalPartCount,
                secretMinimumPartCountToBuild, secretParts);
    }
}
