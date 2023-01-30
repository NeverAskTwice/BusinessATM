import java.util.ArrayList;


public class Bank {
    private final String name;
    private final ArrayList<User> clientList;
    private final ArrayList<Account> accounts;

    User bankUser;
    Account bankAccount;

    /**
     * create a new bank object with empty lists of users and accounts
     * @param name the name of the bank
     */
    public Bank (String name) {
        this.name = name;
        this.clientList = new ArrayList <User>();
        this.accounts = new ArrayList<Account>();
        bankUser =  new User("BANK","LEDGER", "1234", this);
        bankAccount = new Account( this.name, bankUser, AccountType.INTERNAL, this, null);
        this.clientList.add(bankUser);
        this.accounts.add(bankAccount);
    }

    /**
     * Generate a new universally unique ID for a user.
     * @return the user uuid
     */
    public String getNewUserID(){
        // initializations
        String userID;
        int lengthOfUserID = 6;
        boolean nonUnique  = false;
        //  continue looping until we get a unique ID
        do {
            // generate the number
            userID = String.valueOf(System.currentTimeMillis());
            userID = userID.substring(userID.length()-lengthOfUserID, userID.length());
            // check to make sure it's unique
            for(User client: this.clientList){
                if(client.getUserID().equals(userID)) {
                    nonUnique = true;
                    break;
                }
            }
        } while(nonUnique == true);
        return userID;
    }

    /**
     * Get a universally unique Account number for the account
     * @return the account uuid
     */
    public String getNewAccountNumber(){
        // initializations
        String accountNumber;
        int lengthOfAccountNumber = 10;
        boolean nonUnique;
        //  continue looping until we get a unique ID
        do {
            // generate the number
            accountNumber = String.valueOf(System.currentTimeMillis());
            accountNumber = accountNumber.substring(accountNumber.length()-lengthOfAccountNumber, accountNumber.length());

            // check to make sure it's unique
            nonUnique = false;
            for(Account account: this.accounts){
                if(account.getAccountNumber().compareTo(accountNumber) == 0) {
                    nonUnique = true;
                    break;
                }
            }
        } while(nonUnique);
        return accountNumber;
    }

    /**
     * Add an account to the list of accounts maintained by the bank
     * @param newAccount the account to add
     */
    public void addAccount(Account newAccount){

        this.accounts.add(newAccount);
    }

    /**
     * create a new user object and add it to the banks client list
     * @param firstName the first name of the client
     * @param lastName the last name of the client
     * @param pin the pin of the client
     * @return the User object for the client
     */

    public User addClient (String firstName, String lastName, String pin) {
        //create a new User object and add it the bank list of user
        User newClient = new User(firstName, lastName, pin, this);
        this.clientList.add(newClient);

        return newClient;
    }

    /**
     * returns the name of the bank
     * @return the bank name
     */

    public String getBankName(){

        return this.name;
    }

    /**
     * takes a user ID string and pin and validate them and return a user object
     * @param userID the user Unique ID
     * @param pin the user pin
     * @return a User object
     * @throws Exception invalid user id
     */
    public User userLogin(String userID, String pin) throws Exception {
        // search through list of clients
        for (User client: this.clientList){
            //  check user id of specific client is correct
            if (client.getUserID().compareTo(userID)==0 && client.validatePin(pin)) {
                return client;
            }
        }
        throw new Exception("\nInvalid User ID or PIN Combination. Please Try Again.\n");

    }

    /**
     * takes an account number string and returns the account object
     * @param searchAccount account number
     * @return the account object
     * @throws Exception Invalid account
     */
    public Account selectAccountFromListOfAllAccounts(String searchAccount) throws Exception{
        for (Account account : this.accounts){
            if(searchAccount.equals(account.getAccountNumber())){
                return account;
            }
        }
        throw new Exception ("\nInvalid Account Number! Please Try Again\n");
        }

    /**
     * takes a user ID string and gets the user object linked to the ID
     * @param searchUser the user ID
     * @return the User object
     * @throws Exception Invalid User
     */
    public User selectUserFromListOfAllUsers(String searchUser) throws Exception{
        for (User client : this.clientList){
            if(searchUser.equals(client.getUserID())){
                return client;
            }
        }
        throw new Exception ("\nInvalid USER ID! Please Try Again\n");
    }

    /**
     * execute a new transfer transaction
     * @param sourceAccount the account to debit in the transaction
     * @param targetAccount the account to credit in the transaction
     * @param amount the amount of the transaction
     * @param memo a note for the transaction
     */
    public void createNewTransferTransaction(Account sourceAccount, Account targetAccount, double amount, String memo) {
        String targetAccountNumber = targetAccount.getAccountNumber();
        if (sourceAccount.getAccountNumber().equals(targetAccountNumber)){
            System.out.println("TRANSFER CANCELLED! Source and Destination Account Can Not be Same");
        }else{
        String sourceMemo = String.format("£%.02f Transferred to %S -%s",amount, targetAccount.getAccountNumber(), memo);
        String targetMemo = String.format("£%.02f Received from %S - %S",amount, sourceAccount.getAccountNumber(), memo);
        sourceAccount.addTransaction(-1*amount, targetAccountNumber, TransactionType.TRANSFER, sourceMemo);
        targetAccount.addTransaction(amount, targetAccountNumber,TransactionType.TRANSFER, targetMemo);
        bankAccount.addTransaction(-1*amount, String.valueOf(targetAccount),TransactionType.TRANSFER, targetMemo);
        bankAccount.addTransaction(amount, String.valueOf(sourceAccount),TransactionType.TRANSFER, sourceMemo);
        System.out.println("\nTRANSFER SUCCESSFUL!\n");
        }
    }

    /**
     * execute a deposit transaction
     * @param depositAccount the account to credit in the transaction
     * @param bankAccount the account to debit in the transaction
     * @param amount the amount
     * @param memo a note for the transaction
     */
    public void createNewDepositTransaction(Account depositAccount, Account bankAccount, double amount, String memo) {
        String depositAccountNumber = depositAccount.getAccountNumber();
        String depositMemo = String.format("£%.02f Deposited By %s |Note: %s",amount, depositAccount.getPrimaryOwner().getUserDetails().toUpperCase(), memo);

        depositAccount.addTransaction(amount, depositAccountNumber, TransactionType.DEPOSIT, depositMemo);
        bankAccount.addTransaction(-1*amount, depositAccountNumber,TransactionType.DEPOSIT, depositMemo);
        System.out.println("\nDEPOSIT SUCCESSFUL!\n");
    }

    /**
     * execute an overdraft transaction
     * @param overDraftAccount the account to credit
     */
    public void createNewOverdraftTransaction(Account overDraftAccount) {
        String overDraftAccountNumber = overDraftAccount.getAccountNumber();
        double amount;

        amount = overDraftAccount.getAccountType().getAccountOverdraft();
        String overDraftMemo = String.format("£%.02f Overdraft By %s",amount, overDraftAccount.getPrimaryOwner().getUserDetails().toUpperCase());

        overDraftAccount.addTransaction(amount, overDraftAccountNumber, TransactionType.OVERDRAFT, overDraftMemo);
        bankAccount.addTransaction(-1*amount, overDraftAccountNumber,TransactionType.OVERDRAFT, overDraftMemo);
        System.out.println("\nOVERDRAFT SUCCESSFUL!\n");
    }

    /**
     * execute a withdrawal transaction debiting the owner account and crediting the bank internal account
     * @param withdrawalAccount the client account
     * @param bankAccount the bank internal account
     * @param amount the amount
     * @param memo a note attached to the transaction
     */
    public void createNewWithdrawalTransaction(Account withdrawalAccount, Account bankAccount, double amount, String memo) {
        String withdrawalAccountNumber = withdrawalAccount.getAccountNumber();
        String withdrawalMemo = String.format("£%.02f Withdrawn By %s |Note: %s",amount, withdrawalAccount.getPrimaryOwner().getUserDetails().toUpperCase(), memo);

        withdrawalAccount.addTransaction(-1 *amount, withdrawalAccountNumber, TransactionType.WITHDRAWAL, withdrawalMemo);
        bankAccount.addTransaction(amount, withdrawalAccountNumber,TransactionType.WITHDRAWAL, withdrawalMemo);
        System.out.println("\nCASH WITHDRAWN!\n");
    }

    /**
     * delete an account from the banks list of account
     * @param accountToDelete the account to be removed
     */
    public void removeAccount(Account accountToDelete) {
        this.accounts.remove(accountToDelete);
    }
}
