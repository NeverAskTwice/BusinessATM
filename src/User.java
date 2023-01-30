import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
public class User {
    /**
     * The first name of the user.
     */
    private final String firstName;
    /**
     * The last name of the user
     */
    private final String lastName;
    /**
     * The ID number of the user.
     */
    private final String userID;
    /**
     * The hash of the user's pin.
     */
    private byte[] pinHash;
    /**
     * The list of the accounts for this user
     */
    private final ArrayList<Account> accounts;

    /**
     * create a user object
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param pin the pin of the user
     * @param theBank bank object to add the user to
     */
    public User(String firstName, String lastName, String pin, Bank theBank) {
        //set user's name
        this.firstName = firstName;
        this.lastName = lastName;

        // store the pin's MD5 hash, rather that original pin string for security reasons
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            this.pinHash = messagedigest.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();

        }
        // get a unique user id from the bank for the user
        this.userID = theBank.getNewUserID();

        // create empty list of accounts
        this.accounts = new ArrayList<Account>();

    }
    /**
     * Returns the user's first name
     * @return the first name
     */
    public String getFirstName(){
        return this.firstName;
    }

    /**
     * returns the users last name
     * @return the last name
     */
    public String getLastName(){
        return this.lastName;
    }

    /**
     * returns the first and last name of the user
     * @return the first and last name
     */
    public String getUserDetails(){
        return String.format("%s %s", this.firstName, this.lastName);
    }

    /**
     * Add an account for the user
     *
     * @param newAccount the account to add
     */
    public void addAccount(Account newAccount) {
        this.accounts.add(newAccount);
    }

    /**
     * gets the user's uuid
     * @return the uuid
     */
    public String getUserID() {
        return this.userID;
    }


    /**
     * remove an account object from the accounts list
     * @param newAccount the account to be removed
     */
    public void removeAccount(Account newAccount) {
        this.accounts.remove(newAccount);
    }


    /**
     * check whether a given pin matches the true User pin
     * @param inputPin the pin received
     * @return whether the pin is valid or not
     */
    public boolean validatePin(String inputPin) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(messageDigest.digest(inputPin.getBytes()), this.pinHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }
        return false;

    }

    /**
     * print summaries for the accounts of this user.
     */
    public void printAccountsSummary() {
        if (this.numberOfAccounts() == 0) {
            System.out.println("NO ACCOUNT! kindly Create Account");
        } else {

            System.out.printf("\n\n%s's accounts summary", this.getUserDetails().toUpperCase());
            for (int i = 0; i < this.accounts.size(); i++) {
                System.out.printf("\n%d. %s\n", i + 1, this.accounts.get(i).getSummaryLine());
                System.out.println();
            }
            System.out.println();
        }
    }

    /**
     * Get the number of accounts of the user
     * @return the number of accounts
     */
    public int numberOfAccounts() {
        return this.accounts.size();
    }

    /**
     * view the transaction history of an account
     * @param theAccountIndex the index of the account
     */
    public void printAccountTransactionHistory(int theAccountIndex) {

        this.accounts.get(theAccountIndex).printTransactionHistory();

    }

    /**
     * Get the account balance for an account
     * @param account the account to get the balance
     * @return the balance of the account
     */
    public double getAccountBalance(Account account) {
        int index = this.accounts.indexOf(account);
        return (index >= 0) ? this.accounts.get(index).getBalance() : 0;

        }

    /**
     * Get an Account object from an index
     * @param toAccount the index of the account to use
     * @return the UUID of the account
     */
    public Account getAccount(int toAccount) {
        return this.accounts.get(toAccount);
    }


}
