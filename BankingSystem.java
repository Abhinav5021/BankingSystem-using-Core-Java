import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Transaction {
    String timestamp;
    String type;
    double amount;
    double balanceAfter;

    public Transaction(String type, double amount, double balanceAfter) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String toString() {
        return String.format("%-18s | %-15s | %10.2f | %10.2f", timestamp, type, amount, balanceAfter);
    }
}

class Account {
    long accNo;
    String name, address, contact, password;
    double balance;
    List<Transaction> history = new ArrayList<>();

    public Account(long accNo, String name, String address, String contact, String password, double initialDeposit) {
        this.accNo = accNo;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.password = password;
        this.balance = initialDeposit;
        history.add(new Transaction("Initial Deposit", initialDeposit, initialDeposit));
    }
}

public class BankingSystem {
    static Map<Long, Account> db = new HashMap<>();
    static long nextAccNo = 1001;
    static Scanner input = new Scanner(System.in);
    static Account session = null;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            if (session == null) {
                System.out.println("1. Register\n2. Login\n0. Exit");
                int choice = getIntChoice();
                
                if (choice == 1) doRegister();
                else if (choice == 2) doLogin();
                else if (choice == 0) break;
            } else {
                showDashboard();
            }
        }
    }

    static void doRegister() {
        input.nextLine(); 
        System.out.print("Name: "); String name = input.nextLine();
        System.out.print("Address: "); String addr = input.nextLine();
        System.out.print("Contact: "); String phone = input.nextLine();
        System.out.print("Password: "); String pass = input.nextLine();
        System.out.print("Deposit: "); double dep = getDoubleInput();

        Account acc = new Account(nextAccNo++, name, addr, phone, pass, dep);
        db.put(acc.accNo, acc);
        System.out.println("\nAccount created! Your Number: " + acc.accNo);
    }

    static void doLogin() {
        System.out.print("Account No: "); long no = input.nextLong();
        System.out.print("Password: "); String pass = input.next();

        if (db.containsKey(no) && db.get(no).password.equals(pass)) {
            session = db.get(no);
            System.out.println("Welcome, " + session.name);
        } else {
            System.out.println("Invalid login credentials.");
        }
    }

    static void showDashboard() {
        System.out.println("\n1. Profile\n2. Edit Info\n3. Deposit\n4. Withdraw\n5. Transfer\n6. Statement\n0. Logout");
        int choice = getIntChoice();

        switch (choice) {
            case 1 -> {
                System.out.println("Acc: " + session.accNo + "\nOwner: " + session.name + "\nBalance: $" + session.balance);
            }
            case 2 -> {
                input.nextLine();
                System.out.print("New Address: "); session.address = input.nextLine();
                System.out.print("New Contact: "); session.contact = input.nextLine();
                System.out.println("Updated successfully.");
            }
            case 3 -> {
                double amt = getDoubleInput();
                session.balance += amt;
                session.history.add(new Transaction("Deposit", amt, session.balance));
                System.out.println("Success.");
            }
            case 4 -> {
                double amt = getDoubleInput();
                if (amt <= session.balance) {
                    session.balance -= amt;
                    session.history.add(new Transaction("Withdrawal", amt, session.balance));
                } else System.out.println("Insufficient funds.");
            }
            case 5 -> {
                System.out.print("Recipient Acc: "); long target = input.nextLong();
                if (db.containsKey(target) && target != session.accNo) {
                    double amt = getDoubleInput();
                    if (amt <= session.balance) {
                        session.balance -= amt;
                        db.get(target).balance += amt;
                        session.history.add(new Transaction("Sent to " + target, amt, session.balance));
                        db.get(target).history.add(new Transaction("From " + session.accNo, amt, db.get(target).balance));
                        System.out.println("Transfer complete.");
                    } else System.out.println("Insufficient funds.");
                } else System.out.println("Target account invalid.");
            }
            case 6 -> {
                System.out.printf("%-18s | %-15s | %10s | %10s\n", "Date", "Type", "Amount", "Balance");
                for (Transaction t : session.history) System.out.println(t);
            }
            case 0 -> session = null;
        }
    }

    static int getIntChoice() {
        try { return input.nextInt(); } 
        catch (Exception e) { input.next(); return -1; }
    }

    static double getDoubleInput() {
        System.out.print("Amount: ");
        try { return input.nextDouble(); } 
        catch (Exception e) { input.next(); return 0; }
    }
}