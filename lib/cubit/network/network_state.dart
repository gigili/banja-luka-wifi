part of 'network_cubit.dart';

abstract class NetworkState extends Equatable {
  const NetworkState();
}

class NetworkInitial extends NetworkState {
  final List<NetworkModel> networks = [];

  @override
  List<Object> get props => [networks];
}

class NetworkLoading extends NetworkState {
  final List<NetworkModel> networks = [];

  @override
  List<Object> get props => [networks];
}

class NetworkLoaded extends NetworkState {
  final List<NetworkModel> networks;

  NetworkLoaded(this.networks);

  @override
  List<Object> get props => [networks];
}

class NetworkError extends NetworkState {
  final Exception exception;
  final List<NetworkModel> networks = [];

  NetworkError(this.exception);

  @override
  List<Object> get props => [exception, networks];
}
