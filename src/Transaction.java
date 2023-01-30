import java.text.SimpleDateFormat;
import java.util.Date;
public class Transaction {
    /**
     * The amount of the transaction
     */
    private final double amount;
    /**
     * The ID of the transaction
     */
    private final int transactionID;
    /**
     * The time and date of the transaction.
     */
    private final Date transactionDate;
    /**
     * The account performing the transaction
     */
    private final Account sourceAccount;
    /**
     * A reference to the account receiving the amount
     */
    private final String targetAccount;
    /**
     * A memo for the transaction
     */
    private String memo;
    /**
     * the type of transaction
     */
    private final TransactionType transactionType;

    /**
     * creates a transaction object
     * @param amount the amount transacted
     * @param sourceAccount the account generating the transaction
     * @param targetAccount the account receiving the amount
     * @param transactionType the type of transaction
     */
    public Transaction (double amount, Account sourceAccount, String targetAccount, TransactionType transactionType){
        this.amount = amount;
        this.targetAccount = targetAccount;
        this.sourceAccount =sourceAccount;
        this.transactionDate = new Date();
        this.transactionType = transactionType;

        if (sourceAccount.getTransactionsSize() < 0){
            this.transactionID = 1;
        } else{
            this.transactionID =sourceAccount.getTransactionsSize() +1;
        }


    }
    public Transaction (double amount, Account sourceAccount, String targetAccount, TransactionType transactionType, String memo){
    //call the
        this(amount, sourceAccount, targetAccount, transactionType);
        this.memo = memo;

    }

    /**
     * Get the amount of the transaction
     * @return the amount
     */
    public double getAmount() {
        return this.amount;
    }

    /**
     * Get a string summarizing the transaction
     * @return summary string
     *
     */
    public String getSummaryLine() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if(this.amount >=0){
            return String.format("Date: %s |Transaction ID: %s |Amount: £%.02f |Note: %s", formatter.format(this.transactionDate), this.transactionID,
                    this.amount, this.memo );
        } else {
            return String.format("Date: %s |Transaction ID: %s |Amount:  £(%.02f) |Note: %s", formatter.format(this.transactionDate), this.transactionID,
                    this.amount, this.memo );

        }
    }


}
