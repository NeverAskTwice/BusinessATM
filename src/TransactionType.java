public enum TransactionType {

    TRANSFER("transferTransaction"),
    WITHDRAWAL("withdrawTransaction"),
    DEPOSIT("depositTransaction"),
    OVERDRAFT("overdraftTransaction");

    private final String transactionName;

    TransactionType(String transactionName) {
        this.transactionName = transactionName;

    }
    public String getTransactionType(){
        return transactionName;
    }
}