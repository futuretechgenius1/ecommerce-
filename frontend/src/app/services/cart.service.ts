import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface CartItem {
  id: number;
  itemId: number;
  itemName: string;
  itemColor: string;
  itemPrice: number;
  quantity: number;
  subtotal: number;
  gstAmount: number;
  total: number;
  gstPercent: number;
}

export interface Cart {
  id: number;
  items: CartItem[];
  totalItems: number;
  subtotal: number;
  gstAmount: number;
  shippingFee: number;
  totalAmount: number;
  updatedAt: string;
}

export interface AddToCartRequest {
  itemId: number;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  private cartItemCountSubject = new BehaviorSubject<number>(0);
  
  public cart$ = this.cartSubject.asObservable();
  public cartItemCount$ = this.cartItemCountSubject.asObservable();
  
  private apiUrl = `${environment.apiUrl}/api/cart`;

  constructor(private http: HttpClient) {
    this.loadCart();
  }

  loadCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl)
      .pipe(
        tap(cart => {
          this.cartSubject.next(cart);
          this.cartItemCountSubject.next(cart.totalItems);
        })
      );
  }

  addToCart(request: AddToCartRequest): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/items`, request)
      .pipe(
        tap(cart => {
          this.cartSubject.next(cart);
          this.cartItemCountSubject.next(cart.totalItems);
        })
      );
  }

  updateCartItem(cartItemId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/items/${cartItemId}`, { quantity })
      .pipe(
        tap(cart => {
          this.cartSubject.next(cart);
          this.cartItemCountSubject.next(cart.totalItems);
        })
      );
  }

  removeFromCart(cartItemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/items/${cartItemId}`)
      .pipe(
        tap(cart => {
          this.cartSubject.next(cart);
          this.cartItemCountSubject.next(cart.totalItems);
        })
      );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.apiUrl)
      .pipe(
        tap(() => {
          this.cartSubject.next(null);
          this.cartItemCountSubject.next(0);
        })
      );
  }

  get currentCart(): Cart | null {
    return this.cartSubject.value;
  }
}
