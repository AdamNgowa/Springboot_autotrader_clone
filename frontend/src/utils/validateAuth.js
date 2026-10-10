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

export function validateProfile(values) {
  const errors = {};

  if (!values.firstName.trim()) {
    errors.firstName = "First name is required";
  } else if (values.firstName.trim().length > 50) {
    errors.firstName = "First name must not exceed 50 characters";
  }

  if (!values.lastName.trim()) {
    errors.lastName = "Last name is required";
  } else if (values.lastName.trim().length > 50) {
    errors.lastName = "Last name must not exceed 50 characters";
  }

  if (!values.phoneNumber.trim()) {
    errors.phoneNumber = "Phone number is required";
  }

  return errors;
}

export function validatePasswordChange(values) {
  const errors = {};

  if (!values.currentPassword) {
    errors.currentPassword = "Current password is required";
  }

  if (!values.newPassword) {
    errors.newPassword = "New password is required";
  } else if (values.newPassword.length < 8) {
    errors.newPassword = "Password must be at least 8 characters";
  } else if (values.newPassword.length > 100) {
    errors.newPassword = "Password must not exceed 100 characters";
  } else if (values.newPassword === values.currentPassword) {
    errors.newPassword = "New password must be different from the current one";
  }

  if (!values.confirmPassword) {
    errors.confirmPassword = "Please confirm your new password";
  } else if (values.confirmPassword !== values.newPassword) {
    errors.confirmPassword = "Passwords do not match";
  }

  return errors;
}
