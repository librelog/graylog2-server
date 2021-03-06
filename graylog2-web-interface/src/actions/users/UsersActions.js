// @flow strict
import Reflux from 'reflux';
import * as Immutable from 'immutable';

import { singletonActions } from 'views/logic/singleton';
import type { RefluxActions } from 'stores/StoreTypes';
import type { Pagination, PaginatedList } from 'stores/PaginationTypes';
import User, { type UserJSON } from 'logic/users/User';
import UserOverview from 'logic/users/UserOverview';

export type UserCreate = {
  email: $PropertyType<UserJSON, 'email'>,
  full_name: $PropertyType<UserJSON, 'full_name'>,
  password: string,
  permissions: $PropertyType<UserJSON, 'permissions'>,
  roles: $PropertyType<UserJSON, 'roles'>,
  session_timeout_ms: $PropertyType<UserJSON, 'session_timeout_ms'>,
  timezone: $PropertyType<UserJSON, 'timezone'>,
  username: $PropertyType<UserJSON, 'username'>,
};

export type UserUpdate = $Shape<UserCreate & {
  old_password: string,
}>;

export type Token = {
  token_name: string,
  token: string,
  last_access: string,
};

export type ChangePasswordRequest = {
  old_password: string,
  password: string,
};

export type PaginatedUsers = PaginatedList<UserOverview> & {
  adminUser: ?UserOverview,
};

export type ActionsType = {
  create: (user: UserCreate) => Promise<void>,
  load: (userId: string) => Promise<User>,
  loadByUsername: (username: string) => Promise<User>,
  update: (userId: string, request: any) => Promise<void>,
  delete: (userId: string, fullName: string) => Promise<void>,
  changePassword: (userId: string, request: ChangePasswordRequest) => Promise<void>,
  createToken: (userId: string, tokenName: string) => Promise<Token>,
  loadTokens: (userId: string) => Promise<Token[]>,
  deleteToken: (userId: string, tokenId: string, tokenName: string) => Promise<void>,
  loadUsers: () => Promise<Immutable.List<User>>,
  loadUsersPaginated: (pagination: Pagination) => Promise<PaginatedUsers>,
};

const UsersActions: RefluxActions<ActionsType> = singletonActions(
  'Users',
  () => Reflux.createActions({
    create: { asyncResult: true },
    load: { asyncResult: true },
    loadByUsername: { asyncResult: true },
    update: { asyncResult: true },
    delete: { asyncResult: true },
    changePassword: { asyncResult: true },
    createToken: { asyncResult: true },
    loadTokens: { asyncResult: true },
    deleteToken: { asyncResult: true },
    loadUsersPaginated: { asyncResult: true },
    loadUsers: { asyncResult: true },
  }),
);

export default UsersActions;
