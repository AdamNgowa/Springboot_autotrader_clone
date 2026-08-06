export function validateLogin(credentials) {
  const errors = {};

  if (!credentials.email.trim()) {
    errors.email = "Email is required";
  }

  if (!credentials.password) {
    errors.password = "Password is required";
  }

  return errors;
}

export function validateRegister(user) {
  const errors = {};

  if (!user.firstName.trim()) {
    errors.firstName = "First name is required";
  }

  if (!user.lastName.trim()) {
    errors.lastName = "Last name is required";
  }

  if (!user.email.trim()) {
    errors.email = "Email is required";
  }

  if (!user.password) {
    errors.password = "Password is required";
  } else if (user.password.length < 8) {
    errors.password = "Password must be at least 8 characters";
  }

  if (!user.phoneNumber.trim()) {
    errors.phoneNumber = "Phone number is required";
  }

  return errors;
}
