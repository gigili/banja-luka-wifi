import 'package:banjalukawifi/cubit/network/form/network_form_cubit.dart';
import 'package:banjalukawifi/l10n/l10n.dart';
import 'package:banjalukawifi/models/network_model.dart';
import 'package:banjalukawifi/providers/network_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:location/location.dart';
import 'package:network_info_plus/network_info_plus.dart';
import 'package:permission_handler/permission_handler.dart' as perm;

class NetworkFormScreen extends StatefulWidget {
  const NetworkFormScreen({Key? key}) : super(key: key);

  @override
  State<NetworkFormScreen> createState() => _NetworkFormScreenState();
}

class _NetworkFormScreenState extends State<NetworkFormScreen> {
  final GlobalKey<FormState> _formKey = GlobalKey();

  NetworkModel? network;

  final _nameController = TextEditingController();
  final _addressController = TextEditingController();
  final _passwordController = TextEditingController();

  final location = Location();

  double? geoLat;
  double? geoLong;

  @override
  void initState() {
    getWifiInfo();
    super.initState();
  }

  @override
  void didChangeDependencies() {
    setPermissions();
    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) {
    return BlocConsumer<NetworkFormCubit, NetworkFormState>(
      listener: (context, state) {
        if (state is NetworkFormFailed) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(context.l10n.networkSubmittedError)),
          );
        }

        if (state is NetworkFormSubmitted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(context.l10n.networkAwaitingApproval)),
          );
        }
        
        FocusManager.instance.primaryFocus?.unfocus();
      },
      builder: (context, state) {
        if (state is NetworkFormSubmitting) return const Center(child: CircularProgressIndicator());

        return Container(
          padding: EdgeInsets.all(10),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextFormField(
                  enabled: false,
                  controller: _nameController,
                  decoration: InputDecoration(
                    border: const UnderlineInputBorder(borderRadius: BorderRadius.zero),
                    labelText: "${context.l10n.lblNetworkName} *",
                    hintText: context.l10n.lblNetworkName,
                    prefixIcon: const Icon(Icons.wifi),
                  ),
                  keyboardType: TextInputType.text,
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return context.l10n.errorNetworkNameEmpty;
                    }

                    return null;
                  },
                ),
                TextFormField(
                  controller: _passwordController,
                  decoration: InputDecoration(
                    border: const UnderlineInputBorder(borderRadius: BorderRadius.zero),
                    labelText: "${context.l10n.lblNetworkPassword} *",
                    hintText: context.l10n.lblNetworkPassword,
                    prefixIcon: const Icon(Icons.lock),
                  ),
                  keyboardType: TextInputType.visiblePassword,
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return context.l10n.errorNetworkPasswordEmpty;
                    }

                    return null;
                  },
                ),
                TextFormField(
                  controller: _addressController,
                  decoration: InputDecoration(
                    border: const UnderlineInputBorder(borderRadius: BorderRadius.zero),
                    labelText: context.l10n.lblNetworkAddress,
                    hintText: context.l10n.lblNetworkAddress,
                    prefixIcon: const Icon(Icons.location_on),
                  ),
                  keyboardType: TextInputType.streetAddress,
                ),
                const SizedBox(height: 10),
                Text(
                  context.l10n.lblNetworkFormRequiredFieldsNote,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(fontSize: 11, fontStyle: FontStyle.italic),
                ),
                Text(
                  context.l10n.lblNetworkFormDisclaimer,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(fontSize: 11, fontStyle: FontStyle.italic),
                ),
                const SizedBox(height: 10),
                Center(
                  child: ElevatedButton.icon(
                    style: ButtonStyle(
                      minimumSize: MaterialStateProperty.resolveWith((state) => Size(100, 50)),
                    ),
                    icon: Icon(Icons.save),
                    label: Text(
                      context.l10n.save,
                      style: TextStyle(
                        fontSize: 16,
                      ),
                    ),
                    onPressed: () {
                      if ((_formKey.currentState?.validate() ?? false) != true) return;

                      if (geoLat == null || geoLong == null) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text(context.l10n.errorNoLocationAvailable)),
                        );
                        return;
                      }

                      network ??= NetworkModel(
                        id: "unique()",
                        name: _nameController.text,
                        password: _passwordController.text,
                        address: _addressController.text,
                        geoLat: geoLat,
                        geoLong: geoLong,
                      );

                      network?.lastUpdate = DateTime.now().toIso8601String();
                      context.read<NetworkFormCubit>().submitNetwork(network!);
                    },
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Future<perm.PermissionStatus> setPermissions() async {
    final status = await perm.Permission.location.request();

    if (status.isDenied || status.isPermanentlyDenied) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(context.l10n.errorNoLocationPermissions)),
      );
    }

    return status;
  }

  Future<void> getWifiInfo() async {
    final info = NetworkInfo();
    _nameController.text = await info.getWifiName() ?? '';
    final loc = await location.getLocation();
    geoLong = loc.longitude;
    geoLat = loc.latitude;

    if (_nameController.text.isNotEmpty) {
      network = context.read<NetworkProvider>().findNetworkByName(_nameController.text);
      if (network != null) {
        _nameController.text = network?.name ?? "";
        _addressController.text = network?.address ?? "";
        _passwordController.text = network?.password ?? "";
        network?.lastUpdate = DateTime.now().toIso8601String();
      }
    }
  }
}
