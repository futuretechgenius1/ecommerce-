import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hash for user123
        String userPassword = "user123";
        String userHash = encoder.encode(userPassword);
        System.out.println("Password: " + userPassword);
        System.out.println("Hash: " + userHash);
        System.out.println("SQL for user: '$2a$10$' || SUBSTRING('" + userHash + "', 5)");
        
        // Generate hash for admin123
        String adminPassword = "admin123";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("\nPassword: " + adminPassword);
        System.out.println("Hash: " + adminHash);
        System.out.println("SQL for admin: '$2a$10$' || SUBSTRING('" + adminHash + "', 5)");
        
        // Test the existing hash
        String existingHash = "$2a$10$YourHashHere.ReplaceWithActualHashFromDB";
        System.out.println("\nTesting existing hash against 'user123': " + encoder.matches("user123", existingHash));
    }
}
