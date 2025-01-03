package auth;

import java.util.HashMap;

public class MetierAuth {

    /**
     * HashMap pour stocker les couples login, mot de passe
     */
    private HashMap<String, String> authEntries;

    /**
     * constructeur
     */
    public MetierAuth() {
        authEntries = new HashMap<>();
        // ajoute des entrÃ©es de test
        authEntries.put("Toto", "Toto");
        authEntries.put("Titi", "Titi");
        authEntries.put("Tata", "Tata");
        authEntries.put("Tutu", "Tutu");
    }

    /**
     * crÃ©ation d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si Ã§a c'est bien passÃ©
     */
    public synchronized boolean creer(String login, String passwd) {
        if(authEntries.containsKey(login))
            return false; // le login est dÃ©jÃ  prÃ©sent
        authEntries.put(login, passwd); // on l'ajoute
        return true; // Ã§a c'est bien passÃ©
    }

    /**
     *  mise Ã  jour d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si Ã§a c'est bien passÃ©
     */
    public synchronized boolean mettreAJour(String login, String passwd) {
        if(!authEntries.containsKey(login))
            return false; // le login n'est pas prÃ©sent
        authEntries.put(login, passwd); // on remplace le mot de passe
        return true; // Ã§a c'est bien passÃ©
    }

    /**
     *  suppression d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si Ã§a c'est bien passÃ©
     */

    public synchronized boolean supprimer(String login, String passwd) {
        if(!tester(login, passwd))
            return false; // le login ou le mot de passe ne sont pas corrects
        authEntries.remove(login); // on supprime le couple
        return true; // Ã§a c'est bien passÃ©
    }

    /**
     *  test d'un couple (login, mot de passe)
     * @param login : le login
     * @param passwd : le mot de passe
     * @return true si Ã§a c'est bien passÃ©
     */
    public synchronized boolean tester(String login, String passwd) {
        if(!authEntries.containsKey(login))
            return false; // le login n'est pas prÃ©sent
        return authEntries.get(login).equals(passwd); // le mot de passe est correct
// le mot de passe n'est pas correct
    }


}