import { Component, OnInit } from '@angular/core';
import { CatalogService, Product, ProductsResponse } from '../../services/catalog.service';
import { CartService, AddToCartRequest } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-home',
  template: `
    <div class="home-container">
      <!-- Hero Section -->
      <section class="hero-section">
        <div class="hero-content">
          <h1>Welcome to E-Commerce Platform</h1>
          <p>Discover amazing products with the best prices and quality</p>
          <button class="cta-button" (click)="scrollToProducts()">Shop Now</button>
        </div>
      </section>

      <!-- Featured Products Section -->
      <section class="featured-products" id="products">
        <div class="container">
          <h2>Featured Products</h2>
          <div class="products-grid" *ngIf="products.length > 0">
            <div class="product-card" *ngFor="let product of products">
              <div class="product-image">
                <div class="product-placeholder">
                  <i class="material-icons">{{ getProductIcon(product.categoryName || '') }}</i>
                </div>
                <div class="product-badge" [ngClass]="getCategoryClass(product.categoryName || '')">
                  {{ product.categoryName || 'Unknown' }}
                </div>
              </div>
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p class="product-model">{{ product.model }}</p>
                <p class="product-description">{{ product.description }}</p>
                <div class="product-specs">
                  <span class="spec-item">{{ product.color }}</span>
                  <span class="spec-item">{{ product.dimensions }}</span>
                </div>
                <div class="price-section">
                  <div class="price">₹{{ product.price | number:'1.0-0' }}</div>
                  <div class="gst-info">+ {{ product.gstPercent || 0 }}% GST</div>
                </div>
                <div class="stock-info" [ngClass]="{'in-stock': product.inStock, 'out-of-stock': !product.inStock}">
                  <i class="material-icons">{{ product.inStock ? 'check_circle' : 'cancel' }}</i>
                  {{ product.inStock ? 'In Stock (' + (product.stockQty || 0) + ')' : 'Out of Stock' }}
                </div>
                <button 
                  class="add-to-cart-btn" 
                  [disabled]="!(product.inStock ?? true) || isAddingToCart"
                  (click)="addToCart(product)"
                  *ngIf="isLoggedIn()">
                  <i class="material-icons">shopping_cart</i>
                  {{ isAddingToCart ? 'Adding...' : 'Add to Cart' }}
                </button>
                <button 
                  class="login-to-buy-btn" 
                  (click)="goToLogin()"
                  *ngIf="!isLoggedIn()">
                  <i class="material-icons">login</i>
                  Login to Buy
                </button>
              </div>
            </div>
          </div>
          <div class="loading" *ngIf="isLoading">
            <i class="material-icons spinning">refresh</i>
            Loading products...
          </div>
          <div class="error" *ngIf="error">
            <i class="material-icons">error</i>
            {{ error }}
          </div>
        </div>
      </section>

      <!-- Categories Section -->
      <section class="categories-section">
        <div class="container">
          <h2>Shop by Category</h2>
          <div class="categories-grid">
            <div class="category-card electronics">
              <i class="material-icons">devices</i>
              <h3>Electronics</h3>
              <p>Latest gadgets and tech</p>
            </div>
            <div class="category-card home-appliances">
              <i class="material-icons">kitchen</i>
              <h3>Home Appliances</h3>
              <p>Make your home smarter</p>
            </div>
            <div class="category-card fashion">
              <i class="material-icons">checkroom</i>
              <h3>Fashion</h3>
              <p>Trendy clothes and accessories</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .home-container {
      min-height: 100vh;
    }

    .hero-section {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 100px 0;
      text-align: center;
    }

    .hero-content h1 {
      font-size: 3rem;
      margin-bottom: 1rem;
      font-weight: 300;
    }

    .hero-content p {
      font-size: 1.2rem;
      margin-bottom: 2rem;
      opacity: 0.9;
    }

    .cta-button {
      background: #ff6b6b;
      color: white;
      border: none;
      padding: 15px 30px;
      font-size: 1.1rem;
      border-radius: 25px;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .cta-button:hover {
      background: #ff5252;
      transform: translateY(-2px);
    }

    .featured-products {
      padding: 80px 0;
      background: #f8f9fa;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
    }

    .featured-products h2 {
      text-align: center;
      margin-bottom: 50px;
      font-size: 2.5rem;
      color: #333;
    }

    .products-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 30px;
    }

    .product-card {
      background: white;
      border-radius: 15px;
      overflow: hidden;
      box-shadow: 0 10px 30px rgba(0,0,0,0.1);
      transition: all 0.3s ease;
    }

    .product-card:hover {
      transform: translateY(-10px);
      box-shadow: 0 20px 40px rgba(0,0,0,0.15);
    }

    .product-image {
      position: relative;
      height: 200px;
      background: linear-gradient(45deg, #f0f0f0, #e0e0e0);
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .product-placeholder {
      font-size: 4rem;
      color: #999;
    }

    .product-badge {
      position: absolute;
      top: 15px;
      right: 15px;
      padding: 5px 12px;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 500;
      color: white;
    }

    .product-badge.electronics {
      background: #2196F3;
    }

    .product-badge.home-appliances {
      background: #4CAF50;
    }

    .product-badge.fashion {
      background: #E91E63;
    }

    .product-info {
      padding: 20px;
    }

    .product-info h3 {
      margin: 0 0 5px 0;
      font-size: 1.3rem;
      color: #333;
    }

    .product-model {
      color: #666;
      font-size: 0.9rem;
      margin: 0 0 10px 0;
    }

    .product-description {
      color: #777;
      font-size: 0.9rem;
      margin: 0 0 15px 0;
      line-height: 1.4;
    }

    .product-specs {
      display: flex;
      gap: 10px;
      margin-bottom: 15px;
    }

    .spec-item {
      background: #f0f0f0;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 0.8rem;
      color: #666;
    }

    .price-section {
      margin-bottom: 15px;
    }

    .price {
      font-size: 1.5rem;
      font-weight: 600;
      color: #2c3e50;
    }

    .gst-info {
      font-size: 0.8rem;
      color: #666;
    }

    .stock-info {
      display: flex;
      align-items: center;
      gap: 5px;
      margin-bottom: 15px;
      font-size: 0.9rem;
    }

    .stock-info.in-stock {
      color: #4CAF50;
    }

    .stock-info.out-of-stock {
      color: #f44336;
    }

    .add-to-cart-btn, .login-to-buy-btn {
      width: 100%;
      padding: 12px;
      border: none;
      border-radius: 8px;
      font-size: 1rem;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: all 0.3s ease;
    }

    .add-to-cart-btn {
      background: #4CAF50;
      color: white;
    }

    .add-to-cart-btn:hover:not(:disabled) {
      background: #45a049;
    }

    .add-to-cart-btn:disabled {
      background: #ccc;
      cursor: not-allowed;
    }

    .login-to-buy-btn {
      background: #2196F3;
      color: white;
    }

    .login-to-buy-btn:hover {
      background: #1976D2;
    }

    .categories-section {
      padding: 80px 0;
      background: white;
    }

    .categories-section h2 {
      text-align: center;
      margin-bottom: 50px;
      font-size: 2.5rem;
      color: #333;
    }

    .categories-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 30px;
    }

    .category-card {
      text-align: center;
      padding: 40px 20px;
      border-radius: 15px;
      transition: all 0.3s ease;
      cursor: pointer;
    }

    .category-card.electronics {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .category-card.home-appliances {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      color: white;
    }

    .category-card.fashion {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: white;
    }

    .category-card:hover {
      transform: translateY(-10px);
    }

    .category-card i {
      font-size: 3rem;
      margin-bottom: 15px;
    }

    .category-card h3 {
      margin: 0 0 10px 0;
      font-size: 1.5rem;
    }

    .category-card p {
      margin: 0;
      opacity: 0.9;
    }

    .loading, .error {
      text-align: center;
      padding: 40px;
      font-size: 1.1rem;
    }

    .error {
      color: #f44336;
    }

    .spinning {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    @media (max-width: 768px) {
      .hero-content h1 {
        font-size: 2rem;
      }

      .products-grid {
        grid-template-columns: 1fr;
      }

      .categories-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  isLoading = true;
  error = '';
  isAddingToCart = false;

  constructor(
    private catalogService: CatalogService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadFeaturedProducts();
  }

  loadFeaturedProducts() {
    this.isLoading = true;
    console.log('Loading products from:', `${environment.apiUrl}/api/catalog/items`);
    this.catalogService.getProducts().subscribe({
      next: (response: ProductsResponse) => {
        console.log('Products response:', response);
        this.products = response.items || [];
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading products:', error);
        console.error('Full error object:', JSON.stringify(error, null, 2));
        this.error = 'Failed to load products. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  addToCart(product: Product) {
    if (!this.isLoggedIn()) {
      this.goToLogin();
      return;
    }

    this.isAddingToCart = true;
    const request: AddToCartRequest = {
      itemId: product.id,
      quantity: 1
    };
    
    this.cartService.addToCart(request).subscribe({
      next: (response: any) => {
        console.log('Added to cart:', response);
        this.isAddingToCart = false;
        // You could show a success message here
      },
      error: (error: any) => {
        console.error('Error adding to cart:', error);
        this.isAddingToCart = false;
        // You could show an error message here
      }
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isAuthenticated;
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  scrollToProducts() {
    document.getElementById('products')?.scrollIntoView({ behavior: 'smooth' });
  }

  getProductIcon(category: string): string {
    switch (category?.toLowerCase()) {
      case 'electronics':
        return 'devices';
      case 'home appliances':
        return 'kitchen';
      case 'fashion':
        return 'checkroom';
      default:
        return 'shopping_bag';
    }
  }

  getCategoryClass(category: string): string {
    switch (category?.toLowerCase()) {
      case 'electronics':
        return 'electronics';
      case 'home appliances':
        return 'home-appliances';
      case 'fashion':
        return 'fashion';
      default:
        return 'electronics';
    }
  }
}
