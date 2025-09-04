import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  template: `
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>E-Commerce Platform</h3>
          <p>Your trusted online shopping destination</p>
        </div>
        <div class="footer-section">
          <h4>Quick Links</h4>
          <ul>
            <li><a routerLink="/catalog">Products</a></li>
            <li><a routerLink="/about">About Us</a></li>
            <li><a routerLink="/contact">Contact</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>Customer Service</h4>
          <ul>
            <li><a routerLink="/help">Help Center</a></li>
            <li><a routerLink="/returns">Returns</a></li>
            <li><a routerLink="/shipping">Shipping Info</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>Payment Methods</h4>
          <p>UPI • Cards • Cash on Delivery</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 E-Commerce Platform. All rights reserved.</p>
      </div>
    </footer>
  `,
  styles: [`
    .footer {
      background-color: #333;
      color: white;
      margin-top: auto;
    }
    
    .footer-content {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 2rem;
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }
    
    .footer-section h3, .footer-section h4 {
      margin-bottom: 1rem;
      color: #fff;
    }
    
    .footer-section ul {
      list-style: none;
      padding: 0;
    }
    
    .footer-section ul li {
      margin-bottom: 0.5rem;
    }
    
    .footer-section a {
      color: #ccc;
      text-decoration: none;
      transition: color 0.3s;
    }
    
    .footer-section a:hover {
      color: #fff;
    }
    
    .footer-bottom {
      border-top: 1px solid #555;
      padding: 1rem;
      text-align: center;
      background-color: #222;
    }
    
    @media (max-width: 768px) {
      .footer-content {
        grid-template-columns: 1fr;
        gap: 1rem;
        padding: 1rem;
      }
    }
  `]
})
export class FooterComponent { }
