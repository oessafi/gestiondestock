package com.phegondev.InventoryMgtSystem.services.impl;

import com.phegondev.InventoryMgtSystem.dtos.DemandeAchatDTO;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.DemandeAchat;
import com.phegondev.InventoryMgtSystem.models.Product;
import com.phegondev.InventoryMgtSystem.repositories.DemandeAchatRepository;
import com.phegondev.InventoryMgtSystem.repositories.ProductRepository;
import com.phegondev.InventoryMgtSystem.services.DemandeAchatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeAchatServiceImpl implements DemandeAchatService {

    private final DemandeAchatRepository demandeAchatRepository;
    private final ProductRepository productRepository;

    @Override
    public Response createDemande(DemandeAchatDTO dto) {
        Product produit = productRepository.findById(dto.getProduitId())
                .orElseThrow(() -> new NotFoundException("Produit introuvable"));

        DemandeAchat demande = DemandeAchat.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .quantiteDemandee(dto.getQuantiteDemandee())
                .dateDemande(LocalDateTime.now())
                .approuvee(false)
                .produit(produit)
                .build();

        demandeAchatRepository.save(demande);

        return Response.builder()
                .status(200)
                .message("Demande d'achat créée avec succès")
                .build();
    }

    
    @Override
    public Response getAllDemandes() {
        List<DemandeAchatDTO> dtos = demandeAchatRepository.findAll().stream()
                .map(d -> DemandeAchatDTO.builder()
                        .id(d.getId())
                        .titre(d.getTitre())
                        .description(d.getDescription())
                        .quantiteDemandee(d.getQuantiteDemandee())
                        .dateDemande(d.getDateDemande())
                        .approuvee(d.isApprouvee())
                        .produitId(d.getProduit().getId())
                        .produitNom(d.getProduit().getname()) // ✅ AJOUTÉ
                        .build())
                .toList();

        return Response.builder()
                .status(200)
                .message("Liste des demandes")
                .demandes(dtos)
                .build();
    }

    @Override
    public Response getDemandeById(Long id) {
        DemandeAchat d = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demande non trouvée"));

        DemandeAchatDTO dto = DemandeAchatDTO.builder()
                .id(d.getId())
                .titre(d.getTitre())
                .description(d.getDescription())
                .quantiteDemandee(d.getQuantiteDemandee())
                .dateDemande(d.getDateDemande())
                .approuvee(d.isApprouvee())
                .produitId(d.getProduit().getId())
                .build();

        return Response.builder()
                .status(200)
                .message("Demande récupérée")
                .demande(dto)
                .build();
    }

    @Override
    public Response updateDemande(DemandeAchatDTO dto) {
        DemandeAchat demande = demandeAchatRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Demande non trouvée"));

        if (dto.getTitre() != null) demande.setTitre(dto.getTitre());
        if (dto.getDescription() != null) demande.setDescription(dto.getDescription());
        if (dto.getQuantiteDemandee() != null) demande.setQuantiteDemandee(dto.getQuantiteDemandee());
        if (dto.getProduitId() != null) {
            Product produit = productRepository.findById(dto.getProduitId())
                    .orElseThrow(() -> new NotFoundException("Produit introuvable"));
            demande.setProduit(produit);
        }

        demandeAchatRepository.save(demande);

        return Response.builder()
                .status(200)
                .message("Demande mise à jour")
                .build();
    }

    @Override
    public Response deleteDemande(Long id) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demande non trouvée"));

        demandeAchatRepository.delete(demande);

        return Response.builder()
                .status(200)
                .message("Demande supprimée")
                .build();
    }

    @Override
    public Response validerDemande(Long demandeId) {
        DemandeAchat demande = demandeAchatRepository.findById(demandeId)
                .orElseThrow(() -> new NotFoundException("Demande non trouvée"));

        demande.setApprouvee(true);
        demandeAchatRepository.save(demande);

        return Response.builder()
                .status(200)
                .message("Demande approuvée")
                .build();
    }
}
