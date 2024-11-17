import 'dart:async';

import 'package:banjalukawifi/cubit/network/network_cubit.dart';
import "package:flutter/material.dart";
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class NetworkMapScreen extends StatefulWidget {
  const NetworkMapScreen({super.key});

  @override
  State<NetworkMapScreen> createState() => _NetworkMapScreenState();
}

class _NetworkMapScreenState extends State<NetworkMapScreen> {
  final Completer<GoogleMapController> _controller = Completer();

  static const LatLng _center = LatLng(44.769545, 17.189526);
  static const CameraPosition _kGooglePlex = CameraPosition(
    target: _center,
    zoom: 13,
  );

  @override
  void initState() {
    context.read<NetworkCubit>().fetchNetworks(useLocalData: true);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return BlocConsumer<NetworkCubit, NetworkState>(
      listener: (BuildContext ctx, NetworkState state) {},
      builder: (BuildContext ctx, NetworkState state) {
        if (state is NetworkLoading || state is NetworkInitial || state is NetworkError) {
          return Center(child: CircularProgressIndicator());
        }

        final markers = (state as NetworkLoaded)
            .networks
            .map(
              (e) => Marker(
                icon: BitmapDescriptor.defaultMarkerWithHue(
                  HSLColor.fromColor(Theme.of(context).primaryColorDark).hue,
                ),
                markerId: MarkerId(e.id.toString()),
                position: LatLng(e.geoLat!, e.geoLong!),
                infoWindow: InfoWindow(
                  title: e.name,
                  snippet: e.password,
                ),
              ),
            )
            .toSet();

        return GoogleMap(
          mapType: MapType.hybrid,
          initialCameraPosition: _kGooglePlex,
          myLocationEnabled: true,
          onMapCreated: _controller.complete,
          markers: markers,
          mapToolbarEnabled: true,
        );
      },
    );
  }
}
