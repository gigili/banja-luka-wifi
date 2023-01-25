import 'package:banjalukawifi/models/network_model.dart';
import 'package:banjalukawifi/providers/network_provider.dart';
import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';

part 'network_state.dart';

class NetworkCubit extends Cubit<NetworkState> {
  final NetworkProvider provider;

  NetworkCubit(this.provider) : super(NetworkInitial());

  Future<void> fetchNetworks({bool useLocalData = false}) async {
    emit(NetworkLoading());
    List<NetworkModel> networks = await provider.fetchNetworks(useLocalData: useLocalData);
    emit(NetworkLoaded(networks));
  }

  void searchNetwork(String query) {
    emit(NetworkLoading());
    List<NetworkModel> networks = provider.searchNetworksByName(query);
    emit(NetworkLoaded(networks));
  }
}
