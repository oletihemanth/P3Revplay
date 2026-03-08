import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  user = {
    name: '',
    email: '',
    password: '',
    role: 'USER'
  };
  
  message = '';
  isError = false;

  constructor(private authService: AuthService) {}

  onSubmit() {
    this.authService.register(this.user).subscribe({
      next: (response: any) => {
        this.message = 'Account created successfully! You can now log in.';
        this.isError = false;
        this.user = { name: '', email: '', password: '', role: 'USER' };
      },
      error: (err: any) => {
        // Safe navigation operator added to prevent undefined errors
        this.message = err.error?.error || 'Registration failed. Please try again.';
        this.isError = true;
      }
    });
  }
}