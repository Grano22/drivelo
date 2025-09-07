import {Routes} from '@angular/router';
import {authGuard, customerGuard, managerGuard} from './guards/auth.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/dashboard',
        pathMatch: 'full',
    },
    {
        path: 'login',
        loadComponent: () => import('./components/auth/login.component').then(m => m.LoginComponent),
    },
    {
        path: 'unauthorized',
        loadComponent: () => import('./components/common/unauthorized.component').then(m => m.UnauthorizedComponent),
    },
    {
        path: 'dashboard',
        loadComponent: () => import('./components/common/dashboard.component').then(m => m.DashboardComponent),
        canActivate: [authGuard],
    },
    {
        path: 'cars',
        loadComponent: () => import('./components/cars/car-list.component').then(m => m.CarListComponent),
        canActivate: [authGuard, customerGuard],
    },
    {
        path: 'rented-cars',
        loadComponent: () => import('./components/cars/car-rented-list.component').then(m => m.RentedCarListComponent),
        canActivate: [authGuard, customerGuard]
    },
    {
        path: 'rental/:id',
        loadComponent: () => import('./components/cars/car-rental.component').then(m => m.CarRentalComponent),
        canActivate: [authGuard, customerGuard],
    },
    {
        path: 'clients',
        loadComponent: () => import('./components/clients/client-list.component').then(m => m.ClientListComponent),
        canActivate: [authGuard, managerGuard],
    },
    {
        path: 'clients/add',
        loadComponent: () => import('./components/clients/add-client.component').then(m => m.AddClientComponent),
        canActivate: [authGuard, managerGuard],
    },
    {
        path: 'fleet-management',
        loadComponent: () => import('./components/fleet/fleet-management.component').then(m => m.FleetManagementComponent),
        canActivate: [authGuard, managerGuard],
    },
    {
        path: 'fleet-management/add',
        loadComponent: () => import('./components/fleet/add-car.component').then(m => m.AddCarComponent),
        canActivate: [authGuard, managerGuard],
    },
    {
        path: '**',
        redirectTo: '/dashboard',
    },
];