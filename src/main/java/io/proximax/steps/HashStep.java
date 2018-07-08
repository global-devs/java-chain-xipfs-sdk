package io.proximax.steps;


/**
 * The Interface NemHashStep.
 *
 * @param <T> the generic type
 */
public interface HashStep<T> {

    /**
     * Nem hash.
     *
     * @param nemHash the nem hash
     * @return the t
     */
    T nemHash(String nemHash);
    
    
    T ipfsHash(String ipfsHash);
}
