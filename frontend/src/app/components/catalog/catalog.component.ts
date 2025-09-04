import { Component, OnInit } from '@angular/core';
import { CatalogService, Product, Category, ProductsResponse } from '../../services/catalog.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-catalog',
  template: `
    <div class="catalog-container">
      <div class="catalog-header">
        <h2>Product Catalog</h2>
        <div class="search-filters">
          <mat-form-field appearance="outline" class="search-field">
            <mat-label>Search products</mat-label>
            <input matInput [(ngModel)]="searchQuery" (keyup.enter)="searchProducts()" placeholder="Search...">
            <mat-icon matSuffix (click)="searchProducts()">search</mat-icon>
          </mat-form-field>
          
          <mat-form-field appearance="outline" class="filter-field">
            <mat-label>Category</mat-label>
            <mat-select [(value)]="selectedCategory" (selectionChange)="filterByCategory()">
              <mat-option value="">All Categories</mat-option>
              <mat-option *ngFor="let category of categories" [value]="category.id">
                {{category.name}}
              </mat-option>
            </mat-select>
          </mat-form-field>
        </div>
      </div>

      <div class="loading" *ngIf="loading">
        <mat-spinner></mat-spinner>
        <p>Loading products...</p>
      </div>

      <div class="error-message" *ngIf="error">
        <mat-icon>error</mat-icon>
        <p>{{error}}</p>
        <button mat-raised-button color="primary" (click)="loadProducts()">Retry</button>
      </div>

      <div class="products-grid" *ngIf="!loading && !error">
        <mat-card class="product-card" *ngFor="let product of products">
          <mat-card-header>
            <mat-card-title>{{product.name}}</mat-card-title>
            <mat-card-subtitle>{{product.model}}</mat-card-subtitle>
          </mat-card-header>
          
          <mat-card-content>
            <div class="product-info">
              <div class="price">₹{{product.price | number:'1.2-2'}}</div>
              <div class="color-badge" [style.background-color]="getColorCode(product.color)">
                {{product.color}}
              </div>
            </div>
            
            <div class="product-details">
              <p><strong>Dimensions:</strong> {{product.dimensions}}</p>
              <p class="description">{{product.description}}</p>
            </div>

            <div class="attributes" *ngIf="product.attributes && product.attributes.length > 0">
              <h4>Specifications:</h4>
              <div class="attribute" *ngFor="let attr of product.attributes">
                <span class="attr-key">{{attr.attrKey}}:</span>
                <span class="attr-value">{{attr.attrValue}}</span>
              </div>
            </div>
          </mat-card-content>
          
          <mat-card-actions>
            <button mat-raised-button color="primary" 
                    (click)="addToCart(product)" 
                    [disabled]="!isAuthenticated()">
              <mat-icon>add_shopping_cart</mat-icon>
              Add to Cart
            </button>
            <button mat-button color="accent">View Details</button>
          </mat-card-actions>
        </mat-card>
      </div>

      <div class="empty-state" *ngIf="!loading && !error && products.length === 0">
        <mat-icon>inventory_2</mat-icon>
        <h3>No products found</h3>
        <p>Try adjusting your search or filters</p>
      </div>
    </div>
  `,
  styles: [`
    .catalog-container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }

    .catalog-header {
      margin-bottom: 30px;
    }

    .catalog-header h2 {
      margin-bottom: 20px;
      color: #333;
    }

    .search-filters {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
      align-items: center;
    }

    .search-field {
      flex: 1;
      min-width: 300px;
    }

    .filter-field {
      min-width: 200px;
    }

    .loading {
      text-align: center;
      padding: 40px;
    }

    .loading mat-spinner {
      margin: 0 auto 20px;
    }

    .error-message {
      text-align: center;
      padding: 40px;
      color: #f44336;
    }

    .error-message mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
      margin-bottom: 16px;
    }

    .products-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 20px;
      margin-top: 20px;
    }

    .product-card {
      height: fit-content;
    }

    .product-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }

    .price {
      font-size: 24px;
      font-weight: bold;
      color: #2196f3;
    }

    .color-badge {
      padding: 4px 12px;
      border-radius: 16px;
      color: white;
      font-size: 12px;
      font-weight: 500;
      text-shadow: 1px 1px 1px rgba(0,0,0,0.5);
    }

    .product-details p {
      margin: 8px 0;
      color: #666;
    }

    .description {
      font-style: italic;
    }

    .attributes {
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid #eee;
    }

    .attributes h4 {
      margin: 0 0 8px 0;
      font-size: 14px;
      color: #333;
    }

    .attribute {
      display: flex;
      justify-content: space-between;
      margin: 4px 0;
      font-size: 12px;
    }

    .attr-key {
      font-weight: 500;
      color: #666;
    }

    .attr-value {
      color: #333;
    }

    mat-card-actions {
      display: flex;
      justify-content: space-between;
      padding: 16px;
    }

    .empty-state {
      text-align: center;
      padding: 60px 20px;
      color: #666;
    }

    .empty-state mat-icon {
      font-size: 64px;
      width: 64px;
      height: 64px;
      margin-bottom: 16px;
      opacity: 0.5;
    }

    @media (max-width: 768px) {
      .search-filters {
        flex-direction: column;
        align-items: stretch;
      }
      
      .search-field, .filter-field {
        min-width: unset;
      }
      
      .products-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class CatalogComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  loading = false;
  error: string | null = null;
  searchQuery = '';
  selectedCategory: number | string = '';

  constructor(
    private catalogService: CatalogService,
    private cartService: CartService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = null;
    
    const params: any = {};
    if (this.searchQuery) params.q = this.searchQuery;
    if (this.selectedCategory) params.categoryId = this.selectedCategory;

    this.catalogService.getProducts(params).subscribe({
      next: (response: ProductsResponse) => {
        this.products = response.items || [];
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load products. Please try again.';
        this.loading = false;
        console.error('Error loading products:', error);
      }
    });
  }

  loadCategories(): void {
    this.catalogService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  searchProducts(): void {
    this.loadProducts();
  }

  filterByCategory(): void {
    this.loadProducts();
  }

  addToCart(product: Product): void {
    if (!this.isAuthenticated()) {
      this.snackBar.open('Please login to add items to cart', 'Close', { duration: 3000 });
      return;
    }

    this.cartService.addToCart({ itemId: product.id, quantity: 1 }).subscribe({
      next: () => {
        this.snackBar.open(`${product.name} added to cart!`, 'Close', { duration: 3000 });
      },
      error: (error) => {
        this.snackBar.open('Failed to add item to cart', 'Close', { duration: 3000 });
        console.error('Error adding to cart:', error);
      }
    });
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated;
  }

  getColorCode(color: string): string {
    const colorMap: { [key: string]: string } = {
      'Black': '#000000',
      'White': '#FFFFFF',
      'Blue': '#2196F3',
      'Red': '#F44336',
      'Green': '#4CAF50',
      'Silver': '#C0C0C0',
      'Gold': '#FFD700',
      'Gray': '#808080',
      'Grey': '#808080'
    };
    return colorMap[color] || '#666666';
  }
}
