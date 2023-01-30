import java.util.ArrayList;
public class Account {

    /**
     * The name of the account.
     */
    private final String name;

    /**
     * The account ID number.
     */

    private final String accountNumber;
    /**
     * The user object that owns this account
     */

    private final User primaryOwner;
    /**
     * a secondary user object to approve some transactions
     */
    private User secondaryOwner;

    /**
     * The account type object
     */
    private AccountType accountType;

    /**
     * The list of transactions for this account.
     */
    private final ArrayList<Transaction> transactions;


    /**
     * Constructor for account that require only one owner
     * @param name the name of the account
     * @param primaryOwner the user object that owns the account
     * @param accountType   the account type object
     * @param bank the bank that issues the account
     * @param secondaryOwner  optional co-signatory object to approve transactions
     */

    public Account (String name, User primaryOwner, AccountType accountType, Bank bank, User secondaryOwner ){

        this.name = name;
        this.primaryOwner = primaryOwner;
        // get new account UUID
        this.accountNumber = bank.getNewAccountNumber();
        this.accountType = accountType;
        this.transactions = new ArrayList <Transaction>();
        this.secondaryOwner= secondaryOwner;
        this.primaryOwner.addAccount(this);
        bank.addAccount(this);

    }

    /**
     * gets the account's uuid
     * @return the uuid
     */
    public String getAccountNumber(){
        return this.accountNumber;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * gets the account type of account
     * @return the accountType object
     */
    public AccountType getAccountType(){
        return this.accountType;
    }

    /**
     * gets the size of the transactions list
     * @return the size of the transactions list
     */
    public int getTransactionsSize() {
        return this.transactions.size();
    }
    /**
     * Get summary line for the account
     * @return the string summary
     */
    public String getSummaryLine() {
        // get the account's balance
        double balance = this.getBalance();
        //  format the summary line, depending on whether the balance is negative
        if (balance>= 0){
            return String.format("%s : £%.02f : %s | %s ", this.accountNumber, balance, this.name, this.accountType.getAccountName().toUpperCase());
        } else {
            return String.format("%s : £(%.02f) : %s | %s ", this.accountNumber, balance, this.name, this.accountType.getAccountName().toUpperCase());
        }
    }

    /**
     * Get the balance of this account by adding the amounts of the transactions
     * @return the balance value
     */
    public double getBalance() {
        double balance =0;
        for (Transaction t: this.transactions){
            balance += t.getAmount();
        }
        return balance;
    }

    /**
     * Print the transaction history of the account
     */
    public void printTransactionHistory() {
        System.out.printf("\nTransaction History of Account %s\n", this.accountNumber);
        if (this.transactions.size()== 0){
            System.out.println("\nNo Transactions in this Account");
        } else {
            // loops through the transactions list and prints a summary line
            for (int i = this.transactions.size() - 1; i >= 0; i--) {
                System.out.printf(this.transactions.get(i).getSummaryLine());
                System.out.println();
            }
            System.out.println();
        }
    }

    /**
     * Gets the User object that owns the account
     * @return the primary owner
     */
    public User getPrimaryOwner() {
        return primaryOwner;
    }

    /**
     * Adds a new transaction to the list of transactions
     * @param amount amount of the new transaction
     * @param targetAccount a reference to the account receiving/getting the amount
     * @param transactionType the type of the transaction performed
     * @param memo optional note to add to the transaction
     */
    public void addTransaction(double amount, String targetAccount, TransactionType transactionType, String memo) {
        //create new transaction object and add it to our list
        Transaction newTransaction = new Transaction(amount, this, targetAccount, transactionType, memo);
        this.transactions.add(newTransaction);

    }

    /**
     * updates the secondary owner of the account
     * @param secondaryOwner the new secondary owner
     */
    public void setSecondaryOwner(User secondaryOwner){
        this.secondaryOwner =secondaryOwner;
        System.out.println("Co-Signatory ID Updated!");
    }

    /**
     * gets the user object of the secondary owner
     * @return the secondary owner
     */
    public User getSecondaryOwner() {
        return secondaryOwner;
    }


}
