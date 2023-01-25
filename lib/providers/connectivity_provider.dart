import 'package:connectivity_plus/connectivity_plus.dart';

class ConnectivityProvider {
  final Connectivity connectivity;

  ConnectivityProvider(this.connectivity);

  Future<bool> hasConnection() async {
    final status = await connectivity.checkConnectivity();

    if (status == ConnectivityResult.mobile ||
        status == ConnectivityResult.wifi ||
        status == ConnectivityResult.ethernet) {
      return true;
    } else {
      return false;
    }
  }

  Future<bool> isConnectedToWiFi() async {
    final status = await connectivity.checkConnectivity();
    return status == ConnectivityResult.wifi;
  }
}
