import java.util.*;

// 1. Transaction Class for Persistence
class Transaction {
    String date;
    String type;
    double amount;
    double balanceAfter;

    public Transaction(String type, double amount, double balanceAfter) {
        this.date = new Date().toString();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String toString() {
        return String.format("%-25s | %-12s | %-10.2f | %-10.2f", date, type, amount, balanceAfter);
    }
}

// 2. User/Account Class
class Account {
    String name, address, contact, password;
    long accountNumber;
    double balance;
    List<Transaction> history;

    public Account(long accNum, String name, String address, String contact, String password, double initialDeposit) {
        this.accountNumber = accNum;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.password = password;
        this.balance = initialDeposit;
        this.history = new ArrayList<>();
        history.add(new Transaction("Initial", initialDeposit, initialDeposit));
    }
}

// 3. Main Banking System Logic
public class BankingSystem {
    private static Map<Long, Account> database = new HashMap<>();
    private static long accountCounter = 1001; // Starting account number
    private static Scanner sc = new Scanner(System.in);
    private static Account currentUser = null;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- WELCOME TO CORE JAVA BANK ---");
            if (currentUser == null) {
                System.out.println("1. Register\n2. Login\n3. Exit");
                int choice = sc.nextInt();
                if (choice == 1) register();
                else if (choice == 2) login();
                else break;
            } else {
                showUserMenu();
            }
        }
    }

    // FEATURE 1: User Registration
    private static void register() {
        sc.nextLine(); // consume newline
        System.out.print("Enter Name: "); String name = sc.nextLine();
        System.out.print("Enter Address: "); String addr = sc.nextLine();
        System.out.print("Enter Contact: "); String contact = sc.nextLine();
        System.out.print("Set Password: "); String pass = sc.next();
        System.out.print("Initial Deposit: "); double dep = sc.nextDouble();

        Account newAcc = new Account(accountCounter++, name, addr, contact, pass, dep);
        database.put(newAcc.accountNumber, newAcc);
        System.out.println("\nSUCCESS! Your Account Number is: " + newAcc.accountNumber);
    }

    // FEATURE 6: Password Protection (Login)
    private static void login() {
        System.out.print("Account Number: "); long acc = sc.nextLong();
        System.out.print("Password: "); String pass = sc.next();

        if (database.containsKey(acc) && database.get(acc).password.equals(pass)) {
            currentUser = database.get(acc);
            System.out.println("Login Successful. Welcome " + currentUser.name);
        } else {
            System.out.println("Error: Invalid Credentials!");
        }
    }

    private static void showUserMenu() {
        System.out.println("\n1. View Profile\n2. Deposit\n3. Withdraw\n4. Fund Transfer\n5. Statement\n6. Logout");
        int choice = sc.nextInt();
        switch (choice) {
            case 1: 
                System.out.println("Name: " + currentUser.name + "\nBalance: $" + currentUser.balance);
                break;
            case 2: deposit(); break;
            case 3: withdraw(); break;
            case 4: transfer(); break;
            case 5: printStatement(); break;
            case 6: currentUser = null; break;
        }
    }

    // FEATURE 3: Deposit & Withdrawal
    private static void deposit() {
        System.out.print("Enter Amount: "); double amt = sc.nextDouble();
        currentUser.balance += amt;
        currentUser.history.add(new Transaction("Deposit", amt, currentUser.balance));
        System.out.println("Deposit Successful. New Balance: $" + currentUser.balance);
    }

    private static void withdraw() {
        System.out.print("Enter Amount: "); double amt = sc.nextDouble();
        // FEATURE 7: Error Handling
        if (amt > currentUser.balance) {
            System.out.println("ERROR: Insufficient funds!");
        } else {
            currentUser.balance -= amt;
            currentUser.history.add(new Transaction("Withdrawal", amt, currentUser.balance));
            System.out.println("Withdrawal Successful. New Balance: $" + currentUser.balance);
        }
    }

    // FEATURE 4: Fund Transfer
    private static void transfer() {
        System.out.print("Recipient Account Number: "); long recAcc = sc.nextLong();
        if (database.containsKey(recAcc)) {
            System.out.print("Enter Amount: "); double amt = sc.nextDouble();
            if (amt <= currentUser.balance) {
                Account recipient = database.get(recAcc);
                currentUser.balance -= amt;
                recipient.balance += amt;
                
                currentUser.history.add(new Transaction("Transfer To " + recAcc, amt, currentUser.balance));
                recipient.history.add(new Transaction("Received From " + currentUser.accountNumber, amt, recipient.balance));
                
                System.out.println("Transfer Successful!");
            } else {
                System.out.println("ERROR: Insufficient Balance.");
            }
        } else {
            System.out.println("ERROR: Recipient not found.");
        }
    }

    // FEATURE 5: Account Statement
    private static void printStatement() {
        System.out.println("\n--- ACCOUNT STATEMENT ---");
        System.out.printf("%-25s | %-12s | %-10s | %-10s\n", "Date", "Type", "Amount", "Balance");
        for (Transaction t : currentUser.history) {
            System.out.println(t);
        }
    }
}