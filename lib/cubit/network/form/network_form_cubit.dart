import 'package:banjalukawifi/models/network_model.dart';
import 'package:banjalukawifi/providers/network_provider.dart';
import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';

part 'network_form_state.dart';

class NetworkFormCubit extends Cubit<NetworkFormState> {
  final NetworkProvider provider;

  NetworkFormCubit(this.provider) : super(NetworkFormInitial());

  Future<void> submitNetwork(NetworkModel network) async {
    emit(NetworkFormSubmitting());
    try {
      await provider.submitNetwork(network);
      emit(NetworkFormSubmitted());
    } catch (_) {
      emit(NetworkFormFailed());
    }
  }
}
