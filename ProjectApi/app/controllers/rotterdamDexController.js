let mongoose = require('mongoose');
RotterdamDex = mongoose.model('RotterdamDex');

// Create a sample dex
exports.create = ((request, result) => {
    let rotterdamDexEntry = new RotterdamDex({
        name: request.body.name,
        lat: request.body.lat,
        lon: request.body.lon,
        description: request.body.description
    });

    if(request.body.name === "") return false;

    result.send(rotterdamDexEntry);
});

exports.populate = ((request, result ) => {
    const sampleRotterdamDexEntries = [
        {
            1: new RotterdamDex({
                name: 'Euromast',
                lat: '51.9054439',
                lon: '4.4644487',
                description: 'Longest tower of The Netherlands.'
            }),
            2: new RotterdamDex({
                name: 'Diergaarde Blijdorp',
                lat: '51.927354',
                lon: '4.4469521',
                description: 'One of the most popular attractions of Rotterdam.'
            }),
            3: new RotterdamDex({
                name: 'Markthal',
                lat: '51.9201151',
                lon: '4.4847361',
                description: 'Recently build.'
            }),
            4: new RotterdamDex({
                name: 'Hogeschool Rotterdam',
                lat: '51.9171985',
                lon: '4.4818426',
                description: 'University of Rotterdam, where knowledge does not know boundaries.'
            })
        }
    ];

    RotterdamDex.collection.insert(sampleRotterdamDexEntries, () => {
        result.send({message: 'RotterdamDex saved'});
    });

    result.send({message: 'Created a sample for the RotterdamDex'});
});