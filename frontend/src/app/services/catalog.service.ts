import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Product {
  id: number;
  name: string;
  model: string;
  price: number;
  dimensions: string;
  color: string;
  description: string;
  categoryId: number;
  categoryName?: string;
  attributes?: ProductAttribute[];
  stockQty?: number;
  inStock?: boolean;
  gstPercent?: number;
}

export interface ProductAttribute {
  id: number;
  attrKey: string;
  attrValue: string;
}

export interface Category {
  id: number;
  name: string;
}

export interface ProductsResponse {
  totalItems: number;
  totalPages: number;
  hasPrevious: boolean;
  hasNext: boolean;
  currentPage: number;
  items: Product[];
}

@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  private apiUrl = `${environment.apiUrl}/api/catalog`;

  constructor(private http: HttpClient) {}

  getProducts(params?: {
    q?: string;
    categoryId?: number;
    minPrice?: number;
    maxPrice?: number;
    page?: number;
    size?: number;
  }): Observable<ProductsResponse> {
    let httpParams = new HttpParams();
    
    if (params) {
      if (params.q) httpParams = httpParams.set('q', params.q);
      if (params.categoryId) httpParams = httpParams.set('categoryId', params.categoryId.toString());
      if (params.minPrice) httpParams = httpParams.set('minPrice', params.minPrice.toString());
      if (params.maxPrice) httpParams = httpParams.set('maxPrice', params.maxPrice.toString());
      if (params.page) httpParams = httpParams.set('page', params.page.toString());
      if (params.size) httpParams = httpParams.set('size', params.size.toString());
    }

    return this.http.get<ProductsResponse>(`${this.apiUrl}/items`, { params: httpParams });
  }

  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/items/${id}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  searchProducts(query: string): Observable<ProductsResponse> {
    return this.getProducts({ q: query });
  }
}
