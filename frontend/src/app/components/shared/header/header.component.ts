import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-header',
  template: `
    <mat-toolbar color="primary">
      <mat-toolbar-row>
        <span class="logo" routerLink="/home" style="cursor: pointer;">
          <mat-icon>shopping_cart</mat-icon>
          E-Commerce
        </span>
        
        <span class="spacer"></span>
        
        <nav class="nav-links">
          <a mat-button routerLink="/home">Home</a>
          <a mat-button routerLink="/catalog">Products</a>
          
          <ng-container *ngIf="currentUser$ | async as user; else guestMenu">
            <a mat-button routerLink="/cart">
              <mat-icon matBadge="{{cartItemCount$ | async}}" matBadgeColor="accent">shopping_cart</mat-icon>
              Cart
            </a>
            <a mat-button routerLink="/orders">Orders</a>
            <a mat-button routerLink="/profile">Profile</a>
            <a mat-button routerLink="/admin" *ngIf="isAdmin">Admin</a>
            
            <button mat-button [matMenuTriggerFor]="userMenu">
              <mat-icon>account_circle</mat-icon>
              {{user.firstName}}
            </button>
            <mat-menu #userMenu="matMenu">
              <button mat-menu-item routerLink="/profile">
                <mat-icon>person</mat-icon>
                Profile
              </button>
              <button mat-menu-item routerLink="/orders">
                <mat-icon>receipt</mat-icon>
                Order History
              </button>
              <mat-divider></mat-divider>
              <button mat-menu-item (click)="logout()">
                <mat-icon>logout</mat-icon>
                Logout
              </button>
            </mat-menu>
          </ng-container>
          
          <ng-template #guestMenu>
            <a mat-button routerLink="/login">Login</a>
            <a mat-raised-button color="accent" routerLink="/register">Register</a>
          </ng-template>
        </nav>
      </mat-toolbar-row>
    </mat-toolbar>
  `,
  styles: [`
    .spacer {
      flex: 1 1 auto;
    }
    
    .logo {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 1.2em;
      font-weight: 500;
    }
    
    .nav-links {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    
    .nav-links a {
      text-decoration: none;
    }
    
    @media (max-width: 768px) {
      .nav-links a span {
        display: none;
      }
    }
  `]
})
export class HeaderComponent implements OnInit {
  currentUser$: Observable<User | null>;
  cartItemCount$: Observable<number>;
  isAdmin = false;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser;
    this.cartItemCount$ = this.cartService.cartItemCount$;
  }

  ngOnInit(): void {
    this.currentUser$.subscribe(user => {
      this.isAdmin = user?.role === 'ROLE_ADMIN';
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/home']);
  }
}
