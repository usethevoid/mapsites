var SITE_FORM_SCHEMA = [{
    key: "codRAN",
    type: "input",
    templateOptions: {
      "label": "Cod sit RAN"
    }
  }, {
    key: "codLMI",
    type: "input",
    templateOptions: {
      "label": "Cod LMI 2010"
    }
  }, {
    key: "nume",
    type: "input",
    templateOptions: {
      "label": "Nume sit",
      required: true
    }
  }, {
    key: "categorie",
    type: "select",
    templateOptions: {
      "label": "Categorie sit",
      "options": [{
        "name":"",
        "value":undefined
      }
      ,{
          "name": "asezare",
          "value": "asezare",
      }, {
          "name": "locuire",
          "value": "locuire"
      }, {
          "name": "fortificatie",
          "value": "fortificatie"
      }, {
          "name": "necropola",
          "value": "necropola"
      }, {
          "name": "tumul/mormant izolat",
          "value": "tumul/mormant izolat"
      }, {
          "name": "exploatare/cariera/mina",
          "value": "exploatare/cariera/mina"
      }, {
          "name": "drum",
          "value": "drum"
      }, {
          "name": "apeduct",
          "value": "apeduct",
      }
        ]
    }
  },
  {
    key: "tip",
    type: "typeahead",
    templateOptions: {
      "label": "Tip sit",
      "options": ["rural", "urban", "villa", "monastic", "rupestru", "sezonier", "tell", "turn", "castru"]
    }
  }, {
    key: "judet",
    type: "typeahead",
    templateOptions: {
      "label": "Judet",
      "options": ["Constanta", "Tulcea"]
    }
  }, {
    key: "comuna",
    type: "typeahead",
    templateOptions: {
      "label": "Comuna",
      "options": ["Mihail Kogalniceanu", "Targusor", "Gradina", "Pantelimon", "Vulturu", "Cogealac", "Casimcea", "Beidaud", "Stejaru", "Topolog"]
    }
  }, {
    key: "localitate",
    type: "typeahead",
    templateOptions: {
      "label": "Localitate",
      "options": ["Mihail Kogalniceanu", "Piatra", "Palazu Mic", "Targusor", "Mireasa", "Casian", "Cheia", "Gradina", "Pantelimonu de Sus", "Pantelimonu de Jos", "Nistoresti", "Calugareni", "Runcu", "Vulturu", "Ramnicu de Jos", "Ramnicu de Sus", "Gura Dobrogei", "Casimcea", "Razboieni", "Sarighiol de Deal", "Vasile Alecsandri", "Cerbu"]
    }
  }, {
    key: "toponim",
    type: "input",
    templateOptions: {
      "label": "Punct (Toponim)"
    }
  },
  {
    key: "reperCadastral",
    type: "input",
    templateOptions: {
      "label": "Reper cadastral"
    }
  }, {
    key: "reperDrumuri",
    type: "typeahead",
    templateOptions: {
      "label": "Reper cai de comunicatie",
      "options": ["DN 22", "DJ 222", "DJ 222B", "DJ 222E", "DJ 223A", "DJ 225", "DJ 226A", "DJ 226B", "DC 24",
"DC 76", "DC 75", "DC 74", "DC 72", "DC 80", "DC 81", "DC 85"]
    }
  }, {
    key: "repereGeografice",
    type: "textarea",
    templateOptions: {
      "label": "Repere geografice"
    }
  }, {
    key: "reperHidrografic",
    type: "typeahead",
    templateOptions: {
      "label": "Reper hidrografic",
      "options": ["Casimcea", "Dalufac", "Tasaul", "Sitorman", "Targusor", "Valea Seaca (Mireasa)", "Gradina Mucova", "Pantelimon", "Cartal", "Ramnic", "Zandan"]
    }
  }, {
    key: "tipExploatareTeren",
    type: "typeahead",
    templateOptions: {
      "label": "Tip de exploatare a terenului",
      "options": ["agricultura", "pasune", "padure", "locuire", "exploatare miniera"]
    }
  }, {
    key: "suprafata",
    type: "input",
    templateOptions: {
      "label": "Suprafata estimata"
    }
  }, {
    key: "stareConservare",
    type: "select",
    templateOptions: {
      "label": "Stare de conservare",
      "options": [{
        "name": "foarte buna"
      }, {
        "name": "buna"
      }, {
        "name": "medie"
      }, {
        "name": "precara"
      }, {
        "name": "grav afectat"
      }]
    }
  }, {
    key: "factoriRisc",
    type: "typeahead",
    templateOptions: {
      "label": "Factori de risc",
      "options": ["inundatii", "exces de apa in sol", "animale", "alunecari de teren", "exploatare agricola",
"exploatare industriala", "constructii", "imbunatatiri funciare", "depozitare gunoaie"]
    }
  }, {
    key: "istoricCercetare",
    type: "textarea",
    templateOptions: {
      "label": "Istoric cercetare"
    }
  }, {
    key: "descriere",
    type: "textarea",
    templateOptions: {
      "label": "Descriere"
    }
  }, {
    key: "descoperiri",
    type: "textarea",
    templateOptions: {
      "label": "Descoperiri reprezentative"
    }
  }, {
    key: "epoca",
    type: "typeahead",
    templateOptions: {
      "label": "Epoca",
      "options": ["paleolitic", "neolitic", "epoca bronzului", "Hallstatt", "La Tene", "greaca/elenistica", "roman timpuriu", "roman tarziu", "medieval timpuriu", "medieval", "medieval tarziu", "modern"]
    }
  }, {
    key: "cronologie",
    type: "years",
    templateOptions: {
      "label": "Cronologie relativa",
    }
  }, {
    key: "observatii",
    type: "textarea",
    templateOptions: {
      "label": "Observatii"
    }
  }, {
    key: "regimProprietate",
    type: "select",
    templateOptions: {
      "label": "Regim proprietate",
      "options": [{
        "name": "public"
      }, {
        "name": "privat"
      }]
    }
  }, {
    key: "bibliografie",
    type: "textarea",
    templateOptions: {
      "label": "Bibliografie"
    }
  }]
