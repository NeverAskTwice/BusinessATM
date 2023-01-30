import java.util.Scanner;
public class Menu {
    static User currentUser =null;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Bank bank = new Bank("Bank of North East");

        boolean flag = true;
        while(flag) {
            if (currentUser != null) {
                flag = printUserMenu(currentUser, bank, scanner);
            } else {
                currentUser = Menu.mainMenuPrompt(bank, scanner);
            }
        }
    }

    /**
     * prompts a user to log in or create a new user object
     * @param  bank the bank object executing orders
     * @param scanner to receive user input
     * @return an authenticated user object
     */
    private static User mainMenuPrompt(Bank bank, Scanner scanner) {
        // initialization
        String userID;
        String pin;
        User authUser= null;
        // prompt the user for user ID and pin until a correct one is reached and
        // continue looping until successful login

        while(authUser== null){
            System.out.printf("\n\nWelcome to %s ATM\n\n", bank.getBankName());
            System.out.println("Enter user ID: ");
            System.out.println(" ");
            System.out.println("To create new user ID press 0: ");
            try{
                userID = scanner.next();
                if(userID.equals("0")) {
                    authUser = createNewUser(bank, scanner);
                } else{
                    System.out.print("Enter pin");

                    try {
                        pin = scanner.next();
                        authUser = bank.userLogin(userID, pin);
                        //try to get the user object corresponding to the ID and pin
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }

            } catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        return authUser;
    }

    /**
     * provide options for the current user to manage account
     * @param client the current user object
     * @param bank the bank executing orders
     * @param scanner to receive user input
     * @return a boolean to keep the user menu on display
     */
    private static boolean printUserMenu(User client, Bank bank, Scanner scanner){
        int choice;
        do{
            System.out.printf("\nWelcome %s, What would you like to do?\n\n", client.getUserDetails().toUpperCase());
            System.out.println("1. Account Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Request Overdraft");
            System.out.println("6. Manage Account\n");
            System.out.print("Enter Choice: ");
            try{
                choice = Integer.parseInt(scanner.next());
            } catch(Exception e){
                System.out.print(e.getMessage());
                return true;
            }
            if(choice < 0 || choice> 6){
                System.out.println("Invalid Choice. Please choose 1-6");
            }
        } while(choice < 1 || choice > 6);

        if (client.numberOfAccounts() == 0 && choice != 6) client.printAccountsSummary();
        else {
            switch (choice) {
                case 0 -> {
                    return false;
                }
                case 1 -> {
                    client.printAccountsSummary();
                    Menu.showTransactionHistory(client, scanner);
                }
                case 2 -> {
                    client.printAccountsSummary();
                    Menu.withdrawFunds(client, scanner, bank);
                }
                case 3 -> {
                    client.printAccountsSummary();
                    Menu.depositFunds(client, scanner, bank);
                }
                case 4 -> {
                    client.printAccountsSummary();
                    Menu.transferFunds(client, scanner, bank);
                }
                case 5 -> {
                    client.printAccountsSummary();
                    Menu.getOverdraft(client, scanner, bank);
                }
                case 6 -> Menu.manageUserAccount(client, bank, scanner);
            }
        }
        return true;
    }

    /**
     * options to manage the user account
     * @param client the current user object
     * @param bank the bank executing order
     * @param scanner to receive user input
     */
    private static void manageUserAccount(User client, Bank bank, Scanner scanner){
        System.out.println("\n1. Create New Account");
        System.out.println("2. Delete Account");
        System.out.println("3. Change Account Signatory");
        // get user input
       try{
           int choice = scanner.nextInt();
           if (client.numberOfAccounts() == 0 && choice != 1) {
               client.printAccountsSummary();
               return;
           }
           // process user choice
           switch (choice) {
               case 1 -> createNewAccount(client, bank, scanner);
               case 2 -> deleteAccount(client, bank, scanner);
               case 3 -> addSecondaryOwner(client, bank, scanner);
               default -> System.out.println("Invalid choice. Please try again.");
           }
       } catch (Exception e){
           System.out.println(e.getMessage());
       }

    }

    /**
     * receive and check user input before creating a new user object
     * @param bank the bank executing orders
     * @param scanner to receive input
     * @return the new user object
     */

    private static User createNewUser(Bank bank, Scanner scanner) {
        String firstName;
        String lastName;
        String pin;
        boolean inputIsValid;
        do{
            inputIsValid = true;
            System.out.println("Enter first name");
            firstName = scanner.next();
            if(firstName.isEmpty()){
                System.out.println("First name cannot be empty. Please try again.");
                inputIsValid = false;
            }
            System.out.println("Enter last name");
            lastName = scanner.next();
            if(lastName.isEmpty()){
                System.out.println("Last name cannot be empty. Please try again.");
                inputIsValid = false;
            }
            System.out.println("Four digit pin");
            pin = scanner.next();
            if(pin.length() != 4){
                System.out.println("Pin must be four digit. Please try again.");
                inputIsValid = false;
            }
        } while (!inputIsValid);

        User client = bank.addClient(firstName, lastName, pin);

        // print a log message
        System.out.printf("New user %s Created With ID %s . \n", client.getUserDetails().toUpperCase(), client.getUserID());
        return client;
    }

    /**
     * receive and check user input before creating a new account object
     * @param client the current user object
     * @param bank the bank executing the order
     * @param scanner to receive input
     * @return the account object
     */
    private static Account createNewAccount(User client, Bank bank, Scanner scanner) {
        String accountName;
        AccountType accountType = null;
        User secondaryOwner = null;
        Account userAccount;

        //  receive a name for the account
        System.out.println("Enter Name of Account");
        accountName = scanner.next();

        // display the Account types to choose from
        System.out.println("Choose Type of Account");
        for (AccountType type : AccountType.values()) {
            if (!type.equals(AccountType.INTERNAL)) { //User are not allowed to create an internal account type
                System.out.println((type.ordinal() + 1) + ". " + type.getAccountName());
            }
        }
        //  receive account type
        do {
            try {
                int typeChoice = Integer.parseInt(scanner.next());
                if (typeChoice < 1 || typeChoice > AccountType.values().length-1) {
                    System.out.println("Invalid input. Please enter a number between 1 and " + (AccountType.values().length - 1));
                    continue;
                }
                accountType = AccountType.values()[typeChoice-1];
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (accountType == null);

        // receive secondary owner user ID
        if (accountType.equals(AccountType.BUSINESS) || accountType.equals(AccountType.COMMUNITY)) {
            do{
                int choice;
                System.out.println("Select 1 to Enter USER ID for Co-signatory:");
                System.out.println("Select 2 to Create a new User ID for Co-signatory: ");

                try{
                    choice = Integer.parseInt(scanner.next());

                    if (choice == 1) {
                            System.out.println("Enter User ID ->");
                            String secondaryID = scanner.next();
                            secondaryOwner = bank.selectUserFromListOfAllUsers(secondaryID);

                    } else if (choice == 2) {
                        secondaryOwner = createNewUser(bank, scanner);
                    } else {
                        System.out.println("Invalid Choice. Please Try Again!!");
                    }
                } catch(Exception e){
                    System.out.println(e.getMessage());
                }

            }while(secondaryOwner== null);

            userAccount = new Account (accountName, client, accountType, bank, secondaryOwner);
        } else {
            userAccount = new Account (accountName, client, accountType, bank, null);

        }
        System.out.printf("%s Created With %s Number by %s  \n\n", userAccount.getAccountType().getAccountName().toUpperCase(),
                userAccount.getAccountNumber(), userAccount.getPrimaryOwner().getUserDetails().toUpperCase());

        return userAccount;
    }

    /**
     * receive and check user input before updating the secondary owner
     * @param client the current user object
     * @param bank the bank executing the order
     * @param scanner to receive input
     */

    private static void addSecondaryOwner(User client, Bank bank, Scanner scanner) {
        client.printAccountsSummary();
        User secondaryOwner = null;
        Account accountToChangeSignatory = null;

        System.out.println("\nChoose Account Index To Change Co-Signatory ");
        do {
            try {
                int accountIndex = Integer.parseInt(scanner.next())-1;
                accountToChangeSignatory = client.getAccount(accountIndex);

                System.out.println("Enter ID of New Co-Signatory: ");
                String secondaryID = scanner.next();
                secondaryOwner = bank.selectUserFromListOfAllUsers(secondaryID);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while(accountToChangeSignatory == null && secondaryOwner ==null);

        accountToChangeSignatory.setSecondaryOwner(secondaryOwner);

    }

    /**
     * receive and check user input before deleting an account
     * @param client the current user object
     * @param bank bank executing the client order
     * @param scanner to receive input
     */

    private static void deleteAccount(User client, Bank bank, Scanner scanner) {
        int accountIndex =0;
        client.printAccountsSummary();
        System.out.println("Choose Account Index To Delete ");
        if (client.numberOfAccounts() != 0) {
            do {
                try {
                    accountIndex = Integer.parseInt(scanner.next()) - 1;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } while (accountIndex < 0 || accountIndex > client.numberOfAccounts());

            Account accountToDelete = client.getAccount(accountIndex);
            if (accountToDelete.getBalance() != 0) {
                System.out.println("Kindly Dispose Fund Before Closing Account");
            } else {
                client.removeAccount(accountToDelete);
                bank.removeAccount(accountToDelete);
            }
        }
    }


    /**
     * receive and check user input before executing a transfer transaction
     * @param client the current user object
     * @param scanner to receive input
     * @param bank the bank executing the order
     */
    private static void transferFunds(User client, Scanner scanner, Bank bank) {
        // initializations
        Account sourceAccount = null;
        int fromAccount;
        Account targetAccount;
        double amount;
        double accountBalance;
        boolean validAccount = false;
        User secondaryOwner = null;

        // get the account to transfer from
            do {
                System.out.printf("Enter the number (1-%d) of the account to transfer from: ", client.numberOfAccounts());

                try {
                    fromAccount = scanner.nextInt() - 1;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                }
                if (fromAccount < 0 || fromAccount >= client.numberOfAccounts()) {
                    System.out.println("Invalid account. Please try again.");
                } else {
                    sourceAccount = client.getAccount(fromAccount);

                    validAccount = true;
                }
            } while (!validAccount);

            // get the account to transfer to
            try {
                System.out.println("Enter the account to transfer to: ");
                String toAccount = scanner.next();
                targetAccount = bank.selectAccountFromListOfAllAccounts(toAccount);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }

            // get amount to transfer
            accountBalance = client.getAccountBalance(sourceAccount);
            do {
                System.out.printf("Enter the amount to transfer(max £%.02f): £", accountBalance);
                try {
                    amount = scanner.nextDouble();
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                    return;
                }

                if (amount < 0) {
                    System.out.println("Amount must be greater than zero. ");
                } else if (amount > accountBalance) {
                    System.out.printf("Amount must not be greater than balance of £%.02f.\n", accountBalance);
                }
            } while (amount < 0 || amount > accountBalance);

            // get memo for the transfer

            System.out.println("Enter a note for the transaction");
            String memo = scanner.next();

            //  get secondary user approval
            if (sourceAccount.getAccountType().getAccountName().equals(AccountType.COMMUNITY.getAccountName()) ||
                    sourceAccount.getAccountType().getAccountName().equals(AccountType.BUSINESS.getAccountName())) {
                System.out.println("To approve transaction, Enter the User ID of Co-signatory");
                System.out.println("Press 0 to cancel the transaction");

                do {
                    try {
                        String secondaryUserID = scanner.next();
                        if (Integer.parseInt(secondaryUserID) == 0) {
                            break;
                        }
                        User temporaryOwner = bank.selectUserFromListOfAllUsers(secondaryUserID);

                        if (temporaryOwner != sourceAccount.getSecondaryOwner()) {
                            System.out.println("Transaction Not Approved");
                            continue;
                        }

                        secondaryOwner = temporaryOwner;

                        // finally do the transfer for community and business account
                        bank.createNewTransferTransaction(sourceAccount, targetAccount, amount, memo);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } while (secondaryOwner == null);

            } else {
                // do the transfer for client account type
                bank.createNewTransferTransaction(sourceAccount, targetAccount, amount, memo);
            }
        }

    /**
     * receive user input and execute a deposit transaction
     * @param client the client object executing the transaction
     * @param scanner to receive user input
     * @param bank the bank executing the transaction
     */
    private static void depositFunds(User client, Scanner scanner, Bank bank) {
        Account targetAccount;
        double amount = 0d;

            try {
                System.out.print("Enter ACCOUNT NUMBER to Deposit :");
                String toAccount = scanner.next();
                targetAccount = bank.selectAccountFromListOfAllAccounts(toAccount);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
            System.out.print("Enter Amount to Deposit :");
            do {
                try {
                    amount = Double.parseDouble(scanner.next());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } while (amount <= 0);
            System.out.print("Enter a Note :");
            String memo = scanner.next();
            bank.createNewDepositTransaction(targetAccount, bank.bankAccount, amount, memo);
        }

    /**
     * receive and check user input before overdraft transaction
     * @param client the current user object
     * @param scanner to receive input
     * @param bank bank executing the transfer transaction
     */

    private static void getOverdraft(User client, Scanner scanner, Bank bank) {
        int fromAccount;
        User secondaryOwner = null;

        Account overDraftAccount= null;

            do {
                System.out.printf("Enter the Index (1-%d) of Account to get Overdraft on: ", client.numberOfAccounts());
                try {
                    fromAccount = Integer.parseInt(scanner.next()) - 1;
                    if (fromAccount < 0 || fromAccount >= client.numberOfAccounts()) {
                        System.out.println("Invalid Account. Please Try Again.");
                    } else {
                        overDraftAccount = client.getAccount(fromAccount);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } while (overDraftAccount == null);

            // get secondary owner approval

            if (overDraftAccount.getAccountType().getAccountName().equals(AccountType.COMMUNITY.getAccountName()) ||
                    overDraftAccount.getAccountType().getAccountName().equals(AccountType.BUSINESS.getAccountName())) {
                System.out.println("To Approve Transaction. Enter USER ID of Co-signatory");
                System.out.println("Press 0 to Cancel Transaction");

                do {
                    try {
                        String secondaryUserID = scanner.next();
                        if (Integer.parseInt(secondaryUserID) == 0) {
                            break;
                        }
                        User temporaryOwner = bank.selectUserFromListOfAllUsers(secondaryUserID);

                        if (temporaryOwner != overDraftAccount.getSecondaryOwner()) {
                            System.out.println("Transaction Not Approved");
                            continue;
                        }
                        secondaryOwner = temporaryOwner;

                        // finally do the overdraft for community and business account
                        bank.createNewOverdraftTransaction(overDraftAccount);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } while (secondaryOwner == null);

            } else {
                // do the overdraft transaction for client account type
                bank.createNewOverdraftTransaction(overDraftAccount);
            }
        }

    /**
     * receive and check user input before executing a withdrawal transaction
     * @param client the current user object
     * @param scanner to receive input
     * @param bank bank executing the transfer transaction
     */

    private static void withdrawFunds(User client, Scanner scanner, Bank bank) {
        Account withdrawalAccount = null;
        double amount;
        User secondaryOwner = null;
        int withdrawAccountIndex;

            do {
                System.out.printf("Enter Index (1-%d) of Account to Withdraw From : ", client.numberOfAccounts());
                try {
                    withdrawAccountIndex = scanner.nextInt() - 1;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                }

                if (withdrawAccountIndex < 0 || withdrawAccountIndex >= client.numberOfAccounts()) {
                    System.out.println("Invalid Account. Please Try Again!");
                } else {
                    withdrawalAccount = client.getAccount(withdrawAccountIndex);
                }
            } while (withdrawalAccount == null);


            double accountBalance = client.getAccountBalance(withdrawalAccount);
            // get the amount to withdraw
            do {
                System.out.printf("Enter Amount to Withdraw (max £%.02f): £\n", accountBalance);
                try {
                    amount = scanner.nextDouble();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                }
                if (amount < 0) {
                    System.out.println("Amount Must Be Greater Than Zero. \n");
                } else if (amount > accountBalance) {
                    System.out.printf("Amount Can Not Be Greater Than Balance of £%.02f.\n", accountBalance);
                }
            } while (amount < 0 || amount > accountBalance);

            // get memo for withdrawal transaction
            String memo = scanner.nextLine();

            //get secondary user approval for withdrawal transaction

            if (withdrawalAccount.getAccountType().getAccountName().equals(AccountType.COMMUNITY.getAccountName()) ||
                    withdrawalAccount.getAccountType().getAccountName().equals(AccountType.BUSINESS.getAccountName())) {
                System.out.println("To Approve Transaction, Enter USER ID of Co-Signatory");
                System.out.println("Press 0 to cancel Transaction");

                do {
                    try {
                        String secondaryUserID = scanner.next();
                        if (Integer.parseInt(secondaryUserID) == 0) {
                            break;
                        }
                        User temporaryOwner = bank.selectUserFromListOfAllUsers(secondaryUserID);

                        if (temporaryOwner != withdrawalAccount.getSecondaryOwner()) {
                            System.out.println("Transaction Not Approved\n");
                            continue;
                        }

                        secondaryOwner = temporaryOwner;

                        // finally do the withdrawal for community and business account
                        bank.createNewWithdrawalTransaction(withdrawalAccount, bank.bankAccount, amount, memo);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } while (secondaryOwner == null);

            } else {
                // do the withdrawal for client account type
                bank.createNewWithdrawalTransaction(withdrawalAccount, bank.bankAccount, amount, memo);
            }
        }

    /**
     * Show the transaction history for an account
     * @param client the logged-in user object
     * @param scanner the scanner object used for user input
     */
    private static void showTransactionHistory(User client, Scanner scanner) {
        int theAccountIndex =-1;
        // get account whose transaction history to show

            do {
                System.out.printf("Enter Index (1-%d) of Account To Display Transactions : \n", client.numberOfAccounts());
                try{
                    theAccountIndex = scanner.nextInt() - 1;

                } catch(Exception e){
                    System.out.println(e.getMessage());
                    }
                if (theAccountIndex < 0 || theAccountIndex >= client.numberOfAccounts()) {
                    System.out.println("Invalid Account. Please Try Again");
                }
            } while (theAccountIndex < 0 || theAccountIndex >= client.numberOfAccounts());
            // print the transaction history
            client.printAccountTransactionHistory(theAccountIndex);
        }

}
