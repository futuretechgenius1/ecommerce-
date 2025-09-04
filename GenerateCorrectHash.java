import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateCorrectHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("user123 hash: " + encoder.encode("user123"));
        System.out.println("admin123 hash: " + encoder.encode("admin123"));
        
        // Test the existing truncated hash
        String truncatedHash = "$2a$10$Qji2/icFWIGGQEAv8bbwNuKGrSZyiJfDOTKqBJqVPPFdIbG/lSG96";
        System.out.println("Truncated hash length: " + truncatedHash.length());
        System.out.println("Matches user123: " + encoder.matches("user123", truncatedHash));
    }
}
