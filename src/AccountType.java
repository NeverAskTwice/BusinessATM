public enum AccountType {

    BUSINESS("Small Business Account", 1000),
    COMMUNITY ("Community Account", 2500),
    CLIENT ("Client Account", 1500),
    INTERNAL("Bank Operation Account", 0),;


    private final String accountName;
    private final double accountOverdraft;

    AccountType(String name, double accountOverdraft){
        this.accountName = name;
        this.accountOverdraft = accountOverdraft;

    }
    public String getAccountName(){
        return accountName;
    }
    public double getAccountOverdraft(){
        return accountOverdraft;
    }
}
