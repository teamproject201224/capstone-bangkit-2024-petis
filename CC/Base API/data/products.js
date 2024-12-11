const products = [
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/imidakloprid.png',
        name: "Imidakloprid",
        type: "insektisida",
        description: "Insektisida sistemik yang efektif melawan serangga penghisap seperti kutu daun dan wereng.",
        link: "https://www.tokopedia.com/lastore7/avidor-25-wp-100-gram-insektisida-kontak-imidakloprid-pembasmi-hama?extParam=ivf%3Dfalse%26keyword%3Dimidakloprid%26search_id%3D20241207095145318D9B0C7C5292097OHN%26src%3Dsearch"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/fipronil.jpg',
        name: "Fipronil",
        type: "insektisida",
        description: "Insektisida yang digunakan untuk mengendalikan hama tanah dan hama penggerek tanaman.",
        link: "https://www.tokopedia.com/eleventrystore/agadi-50-sc-500-ml-anti-rayap-fipronil-ampuh-rayap-tanah-dan-kayu?extParam=ivf%3Dfalse&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/spinosad.png',
        name: "Spinosad",
        type: "insektisida",
        description: "Insektisida alami yang berasal dari bakteri tanah, efektif untuk mengendalikan berbagai serangga.",
        link: "https://www.tokopedia.com/berkahmulyatanibandung/insektisida-tangker-spinosad-480-sc-250-ml-pestisida-pengendali-hama?extParam=ivf%3Dfalse%26keyword%3Dspinosad%26search_id%3D2024120709533168F2B56C4F40C51C1WK8%26src%3Dsearch"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/symbush.jpg',
        name: "Symbush 50 EC",
        type: "insektisida",
        description: "Insektisida berbasis piretroid yang bekerja cepat untuk mengendalikan berbagai jenis serangga.",
        link: "https://www.tokopedia.com/milkykushop/cymbush-50-ec-100-ml-kemasan-pabrik-insektisida-serangga?extParam=ivf%3Dfalse&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/pumicidine.jpg',
        name: "Pumicidin",
        type: "insektisida",
        description: "Insektisida kontak yang digunakan untuk mengatasi hama di tanaman sayuran dan buah-buahan.",
        link: "https://www.tokopedia.com/zelanproduct/insektisida-penalty-50-sc-fipronil-anti-hama-plus-zpt-setara-regent-50ml"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/vertimec.jpg',
        name: "Vertimec",
        type: "insektisida",
        description: "Insektisida berbasis abamektin yang digunakan untuk mengendalikan tungau dan hama penghisap lainnya.",
        link: "https://www.tokopedia.com/griyatani97/agrimec-18-ec-100-ml?extParam=ivf%3Dfalse&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/dursban.jpg',
        name: "Dursban",
        type: "insektisida",
        description: "Insektisida organofosfat yang digunakan untuk mengendalikan berbagai serangga hama pada tanaman pangan dan hortikultura.",
        link: "https://www.tokopedia.com/milkykushop/dursban-200ec-insektisida-serangga-kemasan-100ml?extParam=ivf%3Dfalse&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/decis.png',
        name: "Decis",
        type: "insektisida",
        description: "Insektisida piretroid sintetis yang efektif melawan berbagai serangga hama pada tanaman.",
        link: "https://www.tokopedia.com/pbasmi/decis-25-ec-insektisida-tanaman-obat-tanaman-bayer-50-ml?extParam=ivf%3Dfalse&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/klorpirifos.jpg',
        name: "Klorpirifos",
        type: "insektisida",
        description: "Insektisida organofosfat yang umum digunakan untuk mengendalikan hama tanah dan tanaman.",
        link: "https://www.tokopedia.com/deki-id/starban-585ec-100-ml-insektisida-klorpirifos-sipermetrin-pembasmi-hama?extParam=ivf%3Dfalse%26keyword%3Dklorpirifos%26search_id%3D20241207100005609E8900BEB0802D0CNN%26src%3Dsearch"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/actara.jpg',
        name: "Actara",
        type: "insektisida",
        description: "Insektisida sistemik yang sangat efektif melawan serangga penghisap dan pengunyah pada berbagai tanaman.",
        link: "https://www.tokopedia.com/tegarmandiri354/insektisida-actara-25wg-2gram-untuk-1tangki-semprot?extParam=ivf%3Dfalse%26keyword%3Dactara%26search_id%3D20241207100102596E24636CC7783B8E6H%26src%3Dsearch"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/neem.jpg',
        name: "Neem Oil",
        type: "bioinsektisida",
        description: "Minyak alami dari biji nimba yang digunakan sebagai bioinsektisida untuk mengendalikan serangga secara organik.",
        link: "https://www.tokopedia.com/panda-farm/250ml-pestisida-organik-neem-oil-minyak-mimba-dari-biosfer-organik?extParam=ivf%3Dtrue&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/beauveria.jpg',
        name: "Beauveria bassiana",
        type: "bioinsektisida",
        description: "Bioinsektisida berbasis jamur yang efektif dalam mengendalikan serangga dengan cara infeksi biologis.",
        link: "https://www.tokopedia.com/distributorpupuk/insektisida-hayati-beauveria-bassiana-hama-trip-obat-pembasmi-hama?extParam=cmp%3D1%26ivf%3Dfalse&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/furadan.jpg',
        name: "Furadan",
        type: "insektisida",
        description: "Insektisida berbasis karbamat yang digunakan untuk mengendalikan hama tanah dan serangga penghisap.",
        link: "https://www.tokopedia.com/pbasmi/furadan-3-gr-insektisida-nematisida-tanaman-fmc-1-kg?extParam=ivf%3Dfalse&src=topads"
    },
    {
        image: 'https://storage.googleapis.com/petis-bucket/products/diazinon.jpg',
        name: "Diazinon",
        type: "insektisida",
        description: "Insektisida spektrum luas yang digunakan untuk mengendalikan berbagai jenis hama serangga.",
        link: "https://www.tokopedia.com/romanagri/diazinon-600ec-isi-500-ml-insektisida-walang-sangit?extParam=ivf%3Dfalse%26keyword%3Ddiazinon%26search_id%3D20241207100306596E24636CC7780A2MPC%26src%3Dsearch"
    }
];

module.exports = products;