package com.atasoft.flangeassist.fragments.paycalc.strategy;

import android.content.res.AssetManager;

import com.atasoft.flangeassist.fragments.paycalc.Province;
import com.atasoft.flangeassist.fragments.paycalc.TaxStatHolder;

import java.util.HashMap;

public class StrategyRepo {
    private final HashMap<Province, ITaxStrategy> _loadedStrategies;
    private final CommonStrategy _commonStrategy;
    private final AssetManager _assetManager;

    public StrategyRepo(AssetManager assetManager) {
        _assetManager = assetManager;
        _loadedStrategies = new HashMap<Province, ITaxStrategy>();
        _commonStrategy = new CommonStrategy(new TaxStatHolder(Province.FED, assetManager));
    }

    public ITaxStrategy get(Province province) {
        if (!_loadedStrategies.containsKey(province)) {
            _loadedStrategies.put(province, loadStrategy(province));
        }

        return _loadedStrategies.get(province);
    }

    public ITaxStrategy get(String provinceName) {
        return get(Province.getProvFromName(provinceName));
    }

    private ITaxStrategy loadStrategy(Province province) {
        switch (province) {
            case AB:
                return new AlbertaTaxStrategy(new TaxStatHolder(province, _assetManager), _commonStrategy);
            case BC:

            default:
                throw new UnsupportedOperationException();
        }
    }
}
