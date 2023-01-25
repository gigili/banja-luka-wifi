part of 'network_form_cubit.dart';

abstract class NetworkFormState extends Equatable {
  const NetworkFormState();
}

class NetworkFormInitial extends NetworkFormState {
  @override
  List<Object> get props => [];
}

class NetworkFormSubmitting extends NetworkFormState {
  @override
  List<Object> get props => [];
}

class NetworkFormSubmitted extends NetworkFormState {
  @override
  List<Object> get props => [];
}

class NetworkFormFailed extends NetworkFormState {
  @override
  List<Object> get props => [];
}
