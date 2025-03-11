import 'package:connectivity_plus/connectivity_plus.dart';

class ConnectivityProvider {
  final Connectivity connectivity;

  ConnectivityProvider(this.connectivity);

  Future<bool> hasConnection() async {
    final statuses = await connectivity.checkConnectivity();
    var hasConnection = false;

    for (final ConnectivityResult status in statuses) {
      if (status == ConnectivityResult.mobile ||
          status == ConnectivityResult.wifi ||
          status == ConnectivityResult.ethernet) {
        hasConnection = true;
        break;
      } else {
        hasConnection = false;
        break;
      }
    }
    return hasConnection;
  }

  Future<bool> isConnectedToWiFi() async {
    final status = await connectivity.checkConnectivity();
    var result = false;
    for(final stat in status){
      if(stat == ConnectivityResult.wifi){
        result = true;
        break;
      }
    }

    return result;
  }
}
