export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserMeResponse {
  id: string;
  username: string;
  email: string;
  displayName: string;
  roles: string[];
  isEnabled: boolean;
}

export interface LoginResponse {
  accessToken: string;
  user: UserMeResponse;
}
