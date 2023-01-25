import 'package:banjalukawifi/models/network_model.dart';
import 'package:banjalukawifi/providers/api_provider.dart';
import 'package:banjalukawifi/providers/box_provider.dart';
import 'package:banjalukawifi/providers/connectivity_provider.dart';

class NetworkProvider {
  final ApiProvider _apiProvider;
  final BoxProvider _boxProvider;
  final ConnectivityProvider _connectivityProvider;

  NetworkProvider({
    required ApiProvider apiProvider,
    required BoxProvider boxProvider,
    required ConnectivityProvider connectivityProvider,
  })  : _apiProvider = apiProvider,
        _boxProvider = boxProvider,
        _connectivityProvider = connectivityProvider;

  Future<List<NetworkModel>> fetchNetworks({bool useLocalData = false}) async {
    List<NetworkModel> networks = [];

    if (useLocalData) {
      networks.addAll(_boxProvider.fetchNetworks());
      if (networks.isNotEmpty) {
        return networks;
      }
    }

    if (await _connectivityProvider.hasConnection()) {
      try {
        networks.addAll(await _apiProvider.fetchNetworks());
        _boxProvider.addNetworks(networks);
      } on Exception catch (_) {
        networks.addAll(_boxProvider.fetchNetworks());
      }
    } else {
      networks.addAll(_boxProvider.fetchNetworks());
    }

    return networks;
  }

  Future<void> submitNetwork(NetworkModel network) async {
    if (!(await _connectivityProvider.hasConnection())) return;
    _apiProvider.submitNetwork(network: network);
  }

  NetworkModel? findNetworkByName(String networkName) {
    return _boxProvider.findNetworkByName(networkName);
  }

  List<NetworkModel> searchNetworksByName(String query) {
    if (query.isEmpty) return _boxProvider.fetchNetworks();
    return _boxProvider.searchNetworksByName(query);
  }
}
