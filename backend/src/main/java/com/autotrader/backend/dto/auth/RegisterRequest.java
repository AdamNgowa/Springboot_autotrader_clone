    package com.autotrader.backend.dto.auth;

    import io.swagger.v3.oas.annotations.media.Schema;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;

    public class RegisterRequest {

        @NotBlank(message = "First name is required")
        @Size(max=50,message = "First name must not exceed 50 characters")
        @Schema(
                description = "User's first name",
                example = "John"
        )
        private String firstName;


        @NotBlank(message  = "Last name is required")
        @Size(max=50,message = "Last name must not exceed 50 characters")
        @Schema(
                description = "User's last name",
                example = "Doe"
        )
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        @Schema(
                description = "User's email address",
                example = "john.doe@example.com"
        )
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min=8,max = 100,
        message = "Password must be between 8 and 100 characters")
        @Schema(
                description = "User's account password",
                example = "SecurePassword123!"
        )
        private String password;

        @NotBlank(message = "Phone number is required")
        @Schema(
                description = "User's contact phone number",
                example = "+254712345678"
        )
        private String phoneNumber;

        public RegisterRequest() {
        }

        public RegisterRequest(String firstName,
                               String lastName,
                               String email,
                               String password,
                               String phoneNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }