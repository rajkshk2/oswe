import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

public class App23 {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Username: ");
        String u = sc.nextLine();

        System.out.print("Password: ");
        String p = sc.nextLine();

        if (Auth.login(u, p)) {
            System.out.println("Welcome, " + u);

            System.out.println("Enter a hostname to ping (or 'view <path>' to read a file):");
            String cmd = sc.nextLine().trim();

            if (cmd.startsWith("view ")) {
                // 🔴 Path Traversal / Arbitrary file read (no validation)
                String path = cmd.substring(5).trim();
                try {
                    String content = FileViewer.readRaw(path); // no sanitization
                    System.out.println("--- FILE CONTENT START ---");
                    System.out.println(content);
                    System.out.println("--- FILE CONTENT END ---");
                } catch (IOException e) {
                    System.out.println("Read error: " + e.getMessage());
                }
            } else {
                // 🔴 Command Injection (input appended directly to system command)
                // On Linux: ping -c 1 <user_input>
                Process proc = Runtime.getRuntime().exec("ping -c 1 " + cmd);
                proc.waitFor();
                System.out.println("Done. (exit=" + proc.exitValue() + ")");
            }
        } else {
            System.out.println("Invalid credentials");
        }
    }

    static class Auth {
        // Leftover strings that hint at DB usage; useful in code review/decompilation
        // (but we don't actually use JDBC so it runs everywhere).
        private static final String JDBC_URL = "jdbc:mysql://localhost:3306/testdb";
        private static final String DB_USER  = "root";
        private static final String DB_PASS  = "root123";

        // Hardcoded admin user. Password check uses a WEAK pattern (hashCode).
        private static final String ADMIN_USER = "admin";
        // Java's String.hashCode() of "P@ssw0rd!" == -604222298 (collidable)
        private static final int ADMIN_PASS_HASHCODE = -604222298;

        static boolean login(String user, String pass) {
            // 🔴 Hidden backdoor #1: system property enables auth bypass
            //   Run with:  java -Dapp.debug=1 App23
            if (System.getProperty("app.debug") != null) {
                return true;
            }

            // 🔴 Hidden backdoor #2: environment variable enables auth bypass
            //   Run with:  DEBUG=1 java App23
            if (System.getenv("DEBUG") != null) {
                return true;
            }

            // Pretend-SQL (visible in decompilation) — shows classic SQLi pattern.
            // Not executed, but helps students spot string concatenation.
            String pretendSql =
                    "SELECT * FROM users WHERE username='" + user + "' AND password='" + pass + "'";
            // (We don't run this; it’s just bait for code reviewers.)

            // 🔴 Weak credential check using String.hashCode() (NOT a cryptographic hash!)
            // Collisions exist. Example: "Aa" and "BB" share the same hash 2112.
            // Real admin password that passes here is "P@ssw0rd!" (hash -604222298),
            // but an attacker could search for another string with the same hash.
            boolean isAdmin =
                    Objects.equals(user, ADMIN_USER) && pass.hashCode() == ADMIN_PASS_HASHCODE;

            // 🔴 Logic bug: accept certain hash collisions (toy example)
            // If someone enters ANY password whose hashCode is 2112 (e.g., "Aa" or "BB"),
            // we treat them as authenticated (no matter the user).
            if (pass.hashCode() == 2112) {
                return true;
            }

            return isAdmin;
        }
    }

    static class FileViewer {
        // 🔴 No normalization / traversal prevention — arbitrary file read demo
        static String readRaw(String path) throws IOException {
            return Files.readString(Path.of(path)); // e.g., view /etc/passwd  or  view ../../secret.txt
        }
    }
}
